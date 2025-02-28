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

@Service
public class DeliveryMethodService {
    public final UserRepository userRepository;
    private final DeliveryTypeRepository deliveryTypeRepository;
    private final CartRepository cartRepository;
    public final DeliveryAddressRepository deliveryAddressRepository;
    private final DeliveryAddressService deliveryAddressService;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private PickupPointDetailsRepository pickupPointDetailsRepository;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryMethodService.class);

    public DeliveryMethodService(UserRepository userRepository, DeliveryTypeRepository deliveryTypeRepository,
                               CartRepository cartRepository, DeliveryAddressRepository deliveryAddressRepository,
                               DeliveryAddressService deliveryAddressService) {
        this.userRepository = userRepository;
        this.deliveryTypeRepository = deliveryTypeRepository;
        this.cartRepository = cartRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.deliveryAddressService = deliveryAddressService;}

    public List<DeliveryTypeDTO> getDeliveryOptions(Long userId) {
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);
        BigDecimal cartTotal = getCartTotal(userId);
        boolean isFreeDelivery = cartTotal.compareTo(new BigDecimal("50.0")) >= 0;
        LocalDate currentDate = LocalDate.now();

        return deliveryTypeRepository.findAll().stream()
                .map(deliveryType -> createDeliveryTypeDTO(deliveryType, currentDate, isFreeDelivery, defaultAddress))
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

        DeliveryType deliveryType = deliveryTypeRepository.findById(request.getDeliveryType().getId())
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Delivery type with ID " + request.getDeliveryType().getId() + " n'a pas été trouvé."));

        // Check order amount
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
    public DeliveryMethodDTO updateDeliveryMethod(Long deliveryMethodId, AddDeliveryMethodRequestDTO request) {
        logger.info("Update delivery mode with ID: {}", deliveryMethodId);

        // Verify the existence of the delivery method
        DeliveryMethod existingDeliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new DeliveryMethodNotFoundException("delivery mode with ID " + deliveryMethodId + " n'a pas été trouvé."));

        // Check user existence
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));

        // Check that the delivery type exists in the database
        DeliveryType deliveryType = deliveryTypeRepository.findById(request.getDeliveryType().getId())
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Delivery type with ID " + request.getDeliveryType().getId() + " n'a pas été trouvé."));

        // Check order amount
        if (request.getOrderAmount() == null || request.getOrderAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new CommandNotFoundException("The order amount is invalid.");
        }

        // Calculation of delivery cost (free delivery if order value >= €50)
        BigDecimal finalDeliveryCost = request.getOrderAmount().compareTo(new BigDecimal("50.0")) >= 0
                ? BigDecimal.ZERO
                : deliveryType.getCost();

        // Update delivery type
        existingDeliveryMethod.setDeliveryType(deliveryType);

        // If the delivery type is a “PICKUP_POINT
        if (DeliveryName.PICKUP_POINT.equals(deliveryType.getDeliveryName())) {
            // Check that the name and address of the collection point are supplied
            if (request.getPickupPointName() == null || request.getPickupPointAddress() == null) {
                throw new PickupPointNotFoundException("The name and address of the collection point are required.");
            }

            // Update or create a new collection point
            PickupPointDetails pickupPoint = existingDeliveryMethod.getPickupPointDetails();
            if (pickupPoint == null) {
                pickupPoint = new PickupPointDetails(
                        request.getPickupPointName(),
                        request.getPickupPointAddress(),
                        request.getPickupPointLatitude(),
                        request.getPickupPointLongitude()
                );
                pickupPointDetailsRepository.save(pickupPoint);
            } else {
                pickupPoint.setPickupPointName(request.getPickupPointName());
                pickupPoint.setPickupPointAddress(request.getPickupPointAddress());
                pickupPoint.setPickupPointLatitude(request.getPickupPointLatitude());
                pickupPoint.setPickupPointLongitude(request.getPickupPointLongitude());
                pickupPointDetailsRepository.save(pickupPoint); // Update the existing point of withdrawal
            }

            existingDeliveryMethod.setPickupPointDetails(pickupPoint);
        } else if (request.getAddressId() != null) {
            // If it's not a collection point, we check the delivery address
            DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found with ID: " + request.getAddressId()));
            existingDeliveryMethod.setDeliveryAddress(deliveryAddress);
        } else {
            throw new IllegalArgumentException("A delivery address or collection point must be specified.");
        }

        // Save updated delivery method
        DeliveryMethod updatedDeliveryMethod = deliveryMethodRepository.save(existingDeliveryMethod);
        logger.info("Delivery mode successfully updated for user ID: {}", user.getId());

        // Update user's shopping cart with delivery method
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found."));
        cart.setDeliveryMethod(updatedDeliveryMethod);
        cart.setDeliveryType(updatedDeliveryMethod.getDeliveryType()); // Update delivery_type_id in Cart

        cartRepository.save(cart);

        return DeliveryMethodDTO.fromEntity(updatedDeliveryMethod);
    }
}
