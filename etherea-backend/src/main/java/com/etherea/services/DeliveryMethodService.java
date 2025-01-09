package com.etherea.services;

import com.etherea.dtos.*;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
     * @param option          The delivery option type.
     * @param deliveryDate    The expected delivery date.
     * @param cost            The delivery cost.
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
     * @param userId         The user ID.
     * @param selectedOption The selected delivery option.
     * @return A `CartWithDeliveryDTO` containing the cart total, delivery cost, and final total.
     */
    public CartWithDeliveryDTO getCartWithDeliveryTotal(Long userId, DeliveryOption selectedOption) {

        // Retrieve and calculate shopping cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Shopping cart not found for user."));

        double cartTotal = cart.calculateTotalAmount().doubleValue();

        // Calculate delivery costs
        double deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, selectedOption);

        // Calculate total
        double total = cartTotal + deliveryCost;

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
     * @throws IllegalArgumentException         If the request data is invalid.
     * @throws UserNotFoundException            If the user is not found.
     * @throws DeliveryAddressNotFoundException If the address is not found for home delivery options.
     */
    @Transactional
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        if (requestDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID must be provided.");
        }
        if (requestDTO.getDeliveryOption() == null) {
            throw new IllegalArgumentException("Delivery option must be provided.");
        }

        // Retrieve and validate user
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + requestDTO.getUserId()));

        // Retrieve address if applicable
        DeliveryAddress deliveryAddress = null;
        if (requestDTO.getDeliveryOption() == DeliveryOption.HOME_STANDARD || requestDTO.getDeliveryOption() == DeliveryOption.HOME_EXPRESS) {
            if (requestDTO.getAddressId() == null) {
                throw new DeliveryAddressNotFoundException("Address ID is required for home delivery.");
            }
            deliveryAddress = deliveryAddressRepository.findById(requestDTO.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Address not found with ID: " + requestDTO.getAddressId()));
        }

        // Create delivery method
        DeliveryMethod deliveryMethod = DeliveryMethodFactory.createDeliveryMethod(
                requestDTO.getDeliveryOption(),
                deliveryAddress,
                requestDTO.getPickupPointName(),
                requestDTO.getPickupPointAddress(),
                requestDTO.getPickupPointLatitude(),
                requestDTO.getPickupPointLongitude(),
                user
        );

        // Set delivery option explicitly
        deliveryMethod.setDeliveryOption(requestDTO.getDeliveryOption());

        // Set the user
        deliveryMethod.setUser(user);

        // Calculate delivery cost and expected date
        double orderAmount = requestDTO.getOrderAmount();
        LocalDate startDate = LocalDate.now();
        deliveryMethod.setDeliveryCost(DeliveryCostCalculator.calculateDeliveryCost(orderAmount, requestDTO.getDeliveryOption()));
        deliveryMethod.setExpectedDeliveryDate(deliveryDateCalculator.calculateDeliveryDate(startDate, deliveryMethod.calculateDeliveryTime()));

        // Save delivery method
        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);

        // Associate delivery method with the user's cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user with ID: " + user.getId()));
        cart.setDeliveryMethod(savedDeliveryMethod);
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
    public void updateDeliveryMethod(UpdateDeliveryMethodRequestDTO requestDTO) {
        logger.info("Received request to update delivery method: {}", requestDTO);

        if (requestDTO == null) {
            logger.error("RequestDTO is null");
            throw new IllegalArgumentException("RequestDTO must not be null");
        }

        if (requestDTO.getDeliveryMethodId() == null) {
            logger.error("Delivery Method ID is null in the request: {}", requestDTO);
            throw new IllegalArgumentException("Delivery Method ID must not be null");
        }

        Long deliveryMethodId = requestDTO.getDeliveryMethodId();
        logger.info("Delivery Method ID: {}", deliveryMethodId);

        // Récupérer le mode de livraison existant
        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> {
                    logger.error("Delivery method not found with ID: {}", deliveryMethodId);
                    return new DeliveryMethodNotFoundException("Delivery method not found with ID: " + deliveryMethodId);
                });
        logger.info("Found delivery method: {}", deliveryMethod);

        // Recherche de l'adresse si nécessaire
        DeliveryAddress deliveryAddress = null;
        if (requestDTO.getDeliveryOption() == DeliveryOption.HOME_STANDARD || requestDTO.getDeliveryOption() == DeliveryOption.HOME_EXPRESS) {
            logger.info("Home delivery selected, checking address ID.");

            Long addressId = requestDTO.getAddressId();  // Utilisez l'ID de l'adresse à partir du DTO
            logger.info("Address ID from request: {}", addressId);

            if (addressId == null) {
                // Si l'adresse est nulle, associer l'adresse existante du mode de livraison actuel
                logger.info("Address ID is null in request, using the existing address for delivery method: {}", deliveryMethod.getDeliveryAddress().getId());
                deliveryAddress = deliveryMethod.getDeliveryAddress();  // Utilisez l'adresse déjà associée au mode de livraison
            } else {
                // Recherche de l'adresse si l'ID est présent dans le DTO
                deliveryAddress = deliveryAddressRepository.findById(addressId)
                        .orElseThrow(() -> {
                            logger.error("Delivery address not found with ID: {}", addressId);
                            return new DeliveryAddressNotFoundException("Address not found with ID: " + addressId);
                        });
                logger.info("Found delivery address: {}", deliveryAddress);
            }
        }

        // Mise à jour du mode de livraison en fonction de l'option choisie
        deliveryMethod.setDeliveryOption(requestDTO.getDeliveryOption());
        deliveryMethod.setDeliveryAddress(deliveryAddress);  // Mettre à jour l'adresse du mode de livraison

        // Mise à jour des informations spécifiques selon le mode de livraison
        if (requestDTO.getDeliveryOption() == DeliveryOption.HOME_STANDARD) {
            logger.info("Switching to HOME_STANDARD delivery");
            if (deliveryMethod instanceof HomeExpressDelivery) {
                HomeExpressDelivery homeExpressDelivery = (HomeExpressDelivery) deliveryMethod;
                homeExpressDelivery.setDeliveryAddress(deliveryAddress);
                homeExpressDelivery.setUser(deliveryMethod.getUser());
                homeExpressDelivery.setDeliveryOption(requestDTO.getDeliveryOption());  // Mise à jour du type
                homeExpressDeliveryRepository.save(homeExpressDelivery); // Sauvegarde l'entité enfant avec l'entité parente
                logger.info("Updated HOME_STANDARD delivery method: {}", homeExpressDelivery);
            } else {
                logger.error("Delivery method is not of type HomeExpressDelivery, cannot switch to HOME_STANDARD");
            }

        } else if (requestDTO.getDeliveryOption() == DeliveryOption.HOME_EXPRESS) {
            logger.info("Switching to HOME_EXPRESS delivery");
            if (deliveryMethod instanceof HomeStandardDelivery homeStandardDelivery) {
                homeStandardDelivery.setDeliveryAddress(deliveryAddress);
                homeStandardDelivery.setUser(deliveryMethod.getUser());
                homeStandardDelivery.setDeliveryOption(requestDTO.getDeliveryOption());
                homeStandardDeliveryRepository.save(homeStandardDelivery);
                logger.info("Updated HOME_EXPRESS delivery method: {}", homeStandardDelivery);
            } else {
                logger.error("Delivery method is not of type HomeStandardDelivery, cannot switch to HOME_EXPRESS");
            }

        } else if (requestDTO.getDeliveryOption() == DeliveryOption.PICKUP_POINT) {
            logger.info("Switching to PICKUP_POINT delivery");
            if (deliveryMethod instanceof PickupPointDelivery pickupPointDelivery) {
                pickupPointDelivery.setPickupPointName("New Pickup Point");
                pickupPointDelivery.setUser(deliveryMethod.getUser());
                pickupPointDelivery.setDeliveryAddress(deliveryAddress);
                pickupPointDelivery.setDeliveryOption(requestDTO.getDeliveryOption());
                pickupPointDeliveryRepository.save(pickupPointDelivery);
                logger.info("Updated PICKUP_POINT delivery method: {}", pickupPointDelivery);
            } else {
                logger.error("Delivery method is not of type PickupPointDelivery, cannot switch to PICKUP_POINT");
            }
        }

        // Sauvegarde du livraison avec l'adresse mise à jour
        deliveryMethodRepository.save(deliveryMethod);
        logger.info("Successfully updated delivery method with ID: {}", deliveryMethodId);
    }

}