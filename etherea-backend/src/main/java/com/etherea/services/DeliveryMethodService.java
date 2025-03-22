package com.etherea.services;

import com.etherea.dtos.*;
import com.etherea.enums.DeliveryName;
import com.etherea.exception.*;
import com.etherea.models.*;
import com.etherea.repositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryMethodService {
    public final UserRepository userRepository;
    private final DeliveryTypeRepository deliveryTypeRepository;
    private final CartRepository cartRepository;
    public final DeliveryAddressRepository deliveryAddressRepository;
    private final DeliveryAddressService deliveryAddressService;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final PickupPointDetailsRepository pickupPointDetailsRepository;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryMethodService.class);

    public DeliveryMethodService(UserRepository userRepository, DeliveryTypeRepository deliveryTypeRepository,
                                 CartRepository cartRepository, DeliveryAddressRepository deliveryAddressRepository,
                                 DeliveryAddressService deliveryAddressService, DeliveryMethodRepository deliveryMethodRepository,
                                 PickupPointDetailsRepository pickupPointDetailsRepository) {
        this.userRepository = userRepository;
        this.deliveryTypeRepository = deliveryTypeRepository;
        this.cartRepository = cartRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.deliveryAddressService = deliveryAddressService;
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.pickupPointDetailsRepository = pickupPointDetailsRepository;}
    public List<DeliveryTypeDTO> getDeliveryOptions(Long userId) {
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);
        BigDecimal cartTotal = getCartTotal(userId);
        boolean isFreeDelivery = cartTotal.compareTo(new BigDecimal("50.0")) >= 0;
        LocalDate currentDate = LocalDate.now();

        return deliveryTypeRepository.findAll().stream()
                .map(deliveryType -> createDeliveryTypeDTO(deliveryType, currentDate, isFreeDelivery, defaultAddress))
                .toList();
    }
    public List<DeliveryMethodDTO> getUserDeliveryMethods(Long userId) {
        List<DeliveryMethod> deliveryMethods = deliveryMethodRepository.findByUserId(userId);

        return deliveryMethods.stream()
                .map(DeliveryMethodDTO::fromEntity)
                .toList();
    }
    private DeliveryAddressDTO getDefaultAddress(Long userId) {
        return deliveryAddressService.getAllDeliveryAddresses(userId)
                .stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("No default address was found for the user."));
    }
    private DeliveryTypeDTO createDeliveryTypeDTO(DeliveryType deliveryType, LocalDate currentDate, boolean isFreeDelivery, DeliveryAddressDTO deliveryAddress) {
        LocalDate estimatedDeliveryDate = currentDate.plusDays(deliveryType.getDeliveryDays());
        BigDecimal finalCost = isFreeDelivery ? BigDecimal.ZERO : deliveryType.getCost();

        return new DeliveryTypeDTO(
                deliveryType.getId(),
                deliveryType.getDeliveryName(),
                deliveryType.getDeliveryDays(),
                finalCost,
                estimatedDeliveryDate
        );
    }
    public CartWithDeliveryDTO getCartWithDeliveryTotal(Long userId, DeliveryName selectedDeliveryName) {
        logger.info("Calculation of shopping cart total with delivery for user {}", userId);
        DeliveryType deliveryType = deliveryTypeRepository.findByDeliveryName(selectedDeliveryName)
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Invalid delivery type: " + selectedDeliveryName));

        BigDecimal cartTotal = getCartTotal(userId);
        BigDecimal deliveryCost = cartTotal.compareTo(new BigDecimal("50.0")) >= 0 ? BigDecimal.ZERO : deliveryType.getCost();

        return new CartWithDeliveryDTO(cartTotal, deliveryCost, cartTotal.add(deliveryCost));
    }
    public BigDecimal getCartTotal(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found for user."))
                .calculateTotalAmount();
    }
    @Transactional
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO request) {
        logger.info("Add delivery mode for user ID: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));

        // Vérifier si le type de livraison existe
        DeliveryType deliveryType = deliveryTypeRepository.findById(request.getDeliveryTypeId())
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Type de livraison introuvable avec l'ID: " + request.getDeliveryTypeId()));

        if (request.getOrderAmount() == null || request.getOrderAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The order amount is invalid.");
        }

        // Calculation of delivery cost (free delivery if order value >= €50)
        BigDecimal finalDeliveryCost = request.getOrderAmount().compareTo(new BigDecimal("50.0")) >= 0
                ? BigDecimal.ZERO
                : deliveryType.getCost();

        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setUser(user);
        deliveryMethod.setDeliveryType(deliveryType);

        if (DeliveryName.PICKUP_POINT.equals(deliveryType.getDeliveryName())) {
            if (request.getPickupPointName() == null || request.getPickupPointAddress() == null) {
                throw new IllegalArgumentException("The name and address of the collection point are required.");
            }
            // Creation of a pick-up point
            PickupPointDetails pickupPoint = new PickupPointDetails(
                    request.getPickupPointName(),
                    request.getPickupPointAddress(),
                    request.getPickupPointLatitude(),
                    request.getPickupPointLongitude()
            );

            pickupPoint = pickupPointDetailsRepository.save(pickupPoint);
            deliveryMethod.setPickupPointDetails(pickupPoint);
        } else if (request.getAddressId() != null) {
            // If it's not a collection point, we check the delivery address
            DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new IllegalArgumentException("Delivery address not found with ID: " + request.getAddressId()));
            deliveryMethod.setDeliveryAddress(deliveryAddress);
        } else {
            throw new IllegalArgumentException("A delivery address or collection point must be specified.");
        }

        // Save delivery method
        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);
        logger.info("Delivery mode successfully registered for user ID: {}", user.getId());

        // Update user's shopping cart with delivery method
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));
        // Link delivery method to cart
        cart.setDeliveryMethod(savedDeliveryMethod);
        cart.setDeliveryType(savedDeliveryMethod.getDeliveryType());  // Update delivery_type_id in Cart

        cartRepository.save(cart);

        return DeliveryMethodDTO.fromEntity(savedDeliveryMethod);
    }
    @Transactional
    public DeliveryMethodDTO updateDeliveryMethod(Long deliveryMethodId, UpdateDeliveryMethodRequestDTO request) {
        logger.info("Update delivery mode with ID: {}", deliveryMethodId);

        // Check existence of delivery method
        DeliveryMethod existingDeliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery mode with ID " + deliveryMethodId + " n'a pas été trouvé."));

        // Check user existence
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));

        // Check that the delivery type exists
        DeliveryType deliveryType = deliveryTypeRepository.findById(request.getDeliveryTypeId())
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Delivery type with ID " + request.getDeliveryTypeId() + " n'a pas été trouvé."));

        // Update delivery type
        existingDeliveryMethod.setDeliveryType(deliveryType);

        if (DeliveryName.PICKUP_POINT.equals(deliveryType.getDeliveryName())) {
            // Check that the relay point information is correct
            if (request.getPickupPointName() == null || request.getPickupPointAddress() == null) {
                throw new PickupPointNotFoundException("The name and address of the collection point are required.");
            }

            // Check if a relay point with the same name and address already exists
            Optional<PickupPointDetails> existingPickupPoint = pickupPointDetailsRepository
                    .findByPickupPointNameAndPickupPointAddress(request.getPickupPointName(), request.getPickupPointAddress());

            PickupPointDetails pickupPoint = existingPickupPoint.orElseGet(() -> {
                // Create a new relay point if none exists
                PickupPointDetails newPickupPoint = new PickupPointDetails(
                        request.getPickupPointName(),
                        request.getPickupPointAddress(),
                        request.getPickupPointLatitude(),
                        request.getPickupPointLongitude()
                );
                return pickupPointDetailsRepository.save(newPickupPoint);
            });

            // Associate the relay point with the delivery method
            existingDeliveryMethod.setPickupPointDetails(pickupPoint);
            existingDeliveryMethod.setDeliveryAddress(null); // Delete home delivery address if necessary

        } else if (request.getAddressId() != null) {
            // If we switch to home delivery
            DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found with ID: " + request.getAddressId()));

            existingDeliveryMethod.setDeliveryAddress(deliveryAddress);

            // We only delete the relationship with the relay point, but not the entry in the table
            existingDeliveryMethod.setPickupPointDetails(null);
        } else {
            throw new IllegalArgumentException("A delivery address or collection point must be specified.");
        }

        DeliveryMethod updatedDeliveryMethod = deliveryMethodRepository.save(existingDeliveryMethod);
        logger.info("Delivery mode successfully updated for user ID: {}", user.getId());

        // Update user's shopping cart with delivery method
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found."));
        cart.setDeliveryMethod(updatedDeliveryMethod);
        cart.setDeliveryType(updatedDeliveryMethod.getDeliveryType());

        cartRepository.save(cart);

        return DeliveryMethodDTO.fromEntity(updatedDeliveryMethod);
    }
}
