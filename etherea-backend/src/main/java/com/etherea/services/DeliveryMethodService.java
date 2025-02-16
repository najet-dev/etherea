package com.etherea.services;

import com.etherea.dtos.CartWithDeliveryDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.DeliveryMethodNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.*;
import com.etherea.factories.DeliveryMethodFactory;
import com.etherea.utils.DeliveryCostCalculator;
import com.etherea.utils.DeliveryDateCalculator;
import com.etherea.utils.HolidayProvider;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DeliveryMethodService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private HomeExpressDeliveryRepository homeExpressDeliveryRepository;
    @Autowired
    private HomeStandardDeliveryRepository homeStandardDeliveryRepository;
    @Autowired
    private PickupPointDeliveryRepository pickupPointDeliveryRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private final DeliveryDateCalculator deliveryDateCalculator;
    public DeliveryMethodService(HolidayProvider holidayProvider) {
        this.deliveryDateCalculator = new DeliveryDateCalculator(holidayProvider.getPublicHolidays());
    }
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    /**
     * Retrieves a list of available delivery options with their costs and estimated dates.
     *
     * @param userId The user ID, needed to fetch the default address.
     * @return A list of delivery options in the form of DTOs.
     */
    public List<DeliveryMethodDTO> getDeliveryOptions(Long userId) {
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);
        double cartTotal = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Shopping cart not found for user."))
                .calculateTotalAmount().doubleValue();

        boolean isFreeDelivery = cartTotal >= 50.0;
        LocalDate currentDate = LocalDate.now();

        // Create the delivery options
        return List.of(
                createDeliveryMethodDTO(DeliveryOption.HOME_STANDARD, currentDate.plusDays(7), isFreeDelivery ? 0.0 : 5.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryOption.HOME_EXPRESS, currentDate.plusDays(2), isFreeDelivery ? 0.0 : 10.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryOption.PICKUP_POINT, currentDate.plusDays(8), isFreeDelivery ? 0.0 : 3.0, null, "")
        );
    }

    /**
     * Retrieves the default delivery address for a given user.
     *
     * @param userId The user ID.
     * @return The user's default delivery address.
     * @throws UserNotFoundException If the user has no default address.
     */
    private DeliveryAddressDTO getDefaultAddress(Long userId) {
        return deliveryAddressService.getAllDeliveryAddresses(userId)
                .stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("No default address found for user."));
    }

    /**
     * Creates a delivery method option of type `DeliveryMethodDTO`.
     *
     * @param option The delivery option type.
     * @param deliveryDate The expected delivery date.
     * @param cost The delivery cost.
     * @param deliveryAddress The delivery address, if applicable.
     * @param pickupPointName The name of the pickup point, if applicable.
     * @return The `DeliveryMethodDTO` instance.
     */
    private DeliveryMethodDTO createDeliveryMethodDTO(
            DeliveryOption option, LocalDate deliveryDate, Double cost,
            DeliveryAddressDTO deliveryAddress, String pickupPointName) {

        return new DeliveryMethodDTO.Builder()
                .setDeliveryOption(option)
                .setExpectedDeliveryDate(deliveryDate)
                .setCost(cost)
                .setDeliveryAddress(deliveryAddress)
                .setPickupPointName(pickupPointName)
                .setPickupPointAddress(option == DeliveryOption.PICKUP_POINT ? "" : null)
                .setPickupPointLatitude(null)
                .setPickupPointLongitude(null)
                .build();
    }

    /**
     * Retrieves the cart and calculates the total with delivery costs based on the selected option.
     *
     * @param userId The user ID.
     * @param selectedOption The selected delivery option.
     * @return A `CartWithDeliveryDTO` containing the cart total, delivery cost, and final total.
     */
    public CartWithDeliveryDTO getCartWithDeliveryTotal(Long userId, DeliveryOption selectedOption) {
        // Retrieve and calculate shopping cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Shopping cart not found for user."));

        // Calculate cart total (items total)
        double cartTotal = cart.calculateTotalAmount().doubleValue();

        // Calculate delivery costs
        double deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, selectedOption);

        // Calculate total with delivery
        double total = cartTotal + deliveryCost;

        // Update the finalTotal in the cart (including the delivery cost)
        cart.setFinalTotal(BigDecimal.valueOf(total));
        cartRepository.save(cart);

        // Return the DTO with the updated totals
        return new CartWithDeliveryDTO(cartTotal, deliveryCost, total);
    }

    /**
     * Retrieves the total amount of the cart for a given user.
     *
     * @param userId The user ID.
     * @return The total amount of the user's cart.
     */
    public double getCartTotal(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Shopping cart not found for user."));
        return cart.calculateTotalAmount().doubleValue();
    }
    /**
     * Adds a delivery method to the user's order.
     *
     * @param requestDTO The DTO containing the delivery method details.
     * @return The DTO of the saved delivery method.
     * @throws IllegalArgumentException If the request data is invalid.
     * @throws UserNotFoundException If the user is not found.
     * @throws DeliveryAddressNotFoundException If the address is not found for home delivery options.
     */
    @Transactional
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO requestDTO) {
        logger.info("Adding delivery method with request: {}", requestDTO);

        if (requestDTO == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        if (requestDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID must be provided.");
        }
        if (requestDTO.getDeliveryOption() == null) {
            throw new IllegalArgumentException("Delivery option must be provided.");
        }

        // Retrieve and validate the user
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + requestDTO.getUserId()));
        logger.debug("User found with ID: {}", user.getId());

        // Retrieve or create the delivery address for home delivery options
        DeliveryAddress deliveryAddress = null;
        if (requestDTO.getDeliveryOption() == DeliveryOption.HOME_STANDARD ||
                requestDTO.getDeliveryOption() == DeliveryOption.HOME_EXPRESS) {
            if (requestDTO.getAddressId() == null) {
                logger.warn("Address ID is required for home delivery, but not provided.");
                throw new DeliveryAddressNotFoundException("Address ID is required for home delivery.");
            }

            deliveryAddress = deliveryAddressRepository.findById(requestDTO.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Address not found with ID: " + requestDTO.getAddressId()));
            logger.debug("Delivery address found with ID: {}", deliveryAddress.getId());
        }

        // Validate relay point information for pickup point delivery
        if (requestDTO.getDeliveryOption() == DeliveryOption.PICKUP_POINT) {
            if (requestDTO.getPickupPointName() == null || requestDTO.getPickupPointName().isBlank() ||
                    requestDTO.getPickupPointAddress() == null || requestDTO.getPickupPointAddress().isBlank() ||
                    requestDTO.getPickupPointLatitude() == null || requestDTO.getPickupPointLongitude() == null) {
                logger.warn("Incomplete pickup point information provided.");
                throw new IllegalArgumentException("Complete pickup point information must be provided for pickup point delivery.");
            }
        }

        // Create the delivery method using the factory
        logger.info("Creating delivery method of type: {}", requestDTO.getDeliveryOption());
        DeliveryMethod deliveryMethod = DeliveryMethodFactory.createDeliveryMethod(
                requestDTO.getDeliveryOption(),
                deliveryAddress,
                requestDTO.getPickupPointName(),
                requestDTO.getPickupPointAddress(),
                requestDTO.getPickupPointLatitude(),
                requestDTO.getPickupPointLongitude(),
                user
        );

        // Calculate and set additional fields
        double orderAmount = requestDTO.getOrderAmount();
        LocalDate startDate = LocalDate.now();
        double deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(orderAmount, requestDTO.getDeliveryOption());
        logger.debug("Calculated delivery cost: {}", deliveryCost);

        deliveryMethod.setDeliveryCost(deliveryCost);
        deliveryMethod.setExpectedDeliveryDate(deliveryDateCalculator.calculateDeliveryDate(startDate, deliveryMethod.calculateDeliveryTime()));

        // Save the delivery method
        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);
        logger.info("Delivery method saved with ID: {}", savedDeliveryMethod.getId());

        // Associate delivery method with the user's cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user with ID: " + user.getId()));
        logger.debug("Cart found for user ID: {}", user.getId());

        cart.setDeliveryMethod(savedDeliveryMethod);

        // Update the total cost in the cart
        double cartTotal = cart.calculateTotalAmount().doubleValue();
        double total = cartTotal + deliveryCost;
        cart.setFinalTotal(BigDecimal.valueOf(total));
        logger.info("Final total for cart ID {} updated to: {}", cart.getId(), total);

        cartRepository.save(cart);

        // Return the DTO
        return DeliveryMethodDTO.fromDeliveryMethod(
                savedDeliveryMethod,
                deliveryAddress,
                startDate,
                orderAmount,
                deliveryDateCalculator
        );
    }
    @Transactional
    public DeliveryMethodDTO updateDeliveryMethod(Long userId, Long deliveryMethodId, AddDeliveryMethodRequestDTO requestDTO) {
        logger.info("Updating delivery method with ID: {}", deliveryMethodId);

        if (requestDTO == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }

        // Vérifier l'existence du mode de livraison
        DeliveryMethod existingDeliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery method not found with ID: " + deliveryMethodId));

        // Vérifier que l'utilisateur possède bien ce mode de livraison
        if (!existingDeliveryMethod.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this delivery method.");
        }

        // Récupérer l'adresse par défaut de l'utilisateur
        List<DeliveryAddress> userAddresses = deliveryAddressRepository.findByUserId(userId);
        DeliveryAddress defaultAddress = userAddresses.stream()
                .filter(DeliveryAddress::isDefault)
                .findFirst()
                .orElseThrow(() -> new DeliveryAddressNotFoundException("Default delivery address not found for user with ID: " + userId));

        // Mettre à jour les champs en fonction du type de livraison
        DeliveryOption newDeliveryOption = requestDTO.getDeliveryOption();
        if (newDeliveryOption != existingDeliveryMethod.getDeliveryOption()) {
            logger.info("Changing delivery method type from {} to {}", existingDeliveryMethod.getDeliveryOption(), newDeliveryOption);

            // Supprimer l'ancien mode de livraison
            deliveryMethodRepository.delete(existingDeliveryMethod);

            // Créer un nouveau mode de livraison
            DeliveryMethod newDeliveryMethod = DeliveryMethodFactory.createDeliveryMethod(
                    newDeliveryOption,
                    defaultAddress,  // Utiliser l'adresse par défaut de l'utilisateur
                    requestDTO.getPickupPointName(),
                    requestDTO.getPickupPointAddress(),
                    requestDTO.getPickupPointLatitude(),
                    requestDTO.getPickupPointLongitude(),
                    existingDeliveryMethod.getUser()
            );

            // Calcul des coûts et de la date de livraison
            double orderAmount = requestDTO.getOrderAmount();
            double newDeliveryCost = DeliveryCostCalculator.calculateDeliveryCost(orderAmount, newDeliveryOption);
            LocalDate newDeliveryDate = deliveryDateCalculator.calculateDeliveryDate(LocalDate.now(), newDeliveryMethod.calculateDeliveryTime());

            newDeliveryMethod.setDeliveryCost(newDeliveryCost);
            newDeliveryMethod.setExpectedDeliveryDate(newDeliveryDate);

            // Sauvegarde du nouveau mode de livraison
            DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(newDeliveryMethod);

            // Mise à jour du panier
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user with ID: " + userId));

            cart.setDeliveryMethod(savedDeliveryMethod);
            cart.setFinalTotal(BigDecimal.valueOf(cart.calculateTotalAmount().doubleValue() + newDeliveryCost));
            cartRepository.save(cart);

            logger.info("Delivery method updated successfully.");
            return DeliveryMethodDTO.fromDeliveryMethod(savedDeliveryMethod, savedDeliveryMethod.getUser().getDefaultAddress(),
                    LocalDate.now(), orderAmount, deliveryDateCalculator);
        } else {
            // Si le type de livraison ne change pas, mettre à jour les infos spécifiques
            if (newDeliveryOption == DeliveryOption.PICKUP_POINT) {
                PickupPointDelivery pickupDelivery = (PickupPointDelivery) existingDeliveryMethod;
                pickupDelivery.setPickupPointName(requestDTO.getPickupPointName());
                pickupDelivery.setPickupPointAddress(requestDTO.getPickupPointAddress());
                pickupDelivery.setPickupPointLatitude(requestDTO.getPickupPointLatitude());
                pickupDelivery.setPickupPointLongitude(requestDTO.getPickupPointLongitude());
            }

            // Mise à jour du coût et de la date de livraison
            double newDeliveryCost = DeliveryCostCalculator.calculateDeliveryCost(requestDTO.getOrderAmount(), newDeliveryOption);
            existingDeliveryMethod.setDeliveryCost(newDeliveryCost);
            existingDeliveryMethod.setExpectedDeliveryDate(deliveryDateCalculator.calculateDeliveryDate(LocalDate.now(),
                    existingDeliveryMethod.calculateDeliveryTime()));

            // Sauvegarde de la mise à jour
            DeliveryMethod updatedDeliveryMethod = deliveryMethodRepository.save(existingDeliveryMethod);

            // Mise à jour du panier
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user with ID: " + userId));

            cart.setFinalTotal(BigDecimal.valueOf(cart.calculateTotalAmount().doubleValue() + newDeliveryCost));
            cartRepository.save(cart);

            logger.info("Delivery method details updated successfully.");
            return DeliveryMethodDTO.fromDeliveryMethod(updatedDeliveryMethod, updatedDeliveryMethod.getUser().getDefaultAddress(),
                    LocalDate.now(), requestDTO.getOrderAmount(), deliveryDateCalculator);
        }
    }

}