package com.etherea.services;

import com.etherea.dtos.*;
import com.etherea.enums.DeliveryName;
import com.etherea.exception.*;
import com.etherea.models.*;
import com.etherea.repositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
public class DeliveryMethodService {
    private final UserRepository userRepository;
    private final DeliveryTypeRepository deliveryTypeRepository;
    private final CartRepository cartRepository;
    private final DeliveryAddressService deliveryAddressService;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryMethodService.class);
    public DeliveryMethodService(UserRepository userRepository, DeliveryTypeRepository deliveryTypeRepository,
                                 CartRepository cartRepository, DeliveryAddressService deliveryAddressService, DeliveryMethodRepository deliveryMethodRepository) {
        this.userRepository = userRepository;
        this.deliveryTypeRepository = deliveryTypeRepository;
        this.cartRepository = cartRepository;
        this.deliveryAddressService = deliveryAddressService;
        this.deliveryMethodRepository = deliveryMethodRepository;
    }
    /**
     * Retrieves available delivery options for a user based on their default address and cart total.
     *
     * @param userId the ID of the user
     * @return a list of available {@link DeliveryTypeDTO} options
     */
    public List<DeliveryTypeDTO> getDeliveryOptions(Long userId) {
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);
        BigDecimal cartTotal = getCartTotal(userId);
        boolean isFreeDelivery = cartTotal.compareTo(new BigDecimal("50.0")) >= 0;
        LocalDate currentDate = LocalDate.now();

        return deliveryTypeRepository.findAll().stream()
                .map(deliveryType -> createDeliveryTypeDTO(deliveryType, currentDate, isFreeDelivery, defaultAddress))
                .toList();
    }
    /**
     * Retrieves all delivery methods previously chosen by the user.
     *
     * @param userId the ID of the user
     * @return a list of {@link DeliveryMethodDTO} records
     */
    public List<DeliveryMethodDTO> getUserDeliveryMethods(Long userId) {
        return deliveryMethodRepository.findByUserId(userId)
                .stream()
                .map(DeliveryMethodDTO::fromEntity)
                .toList();
    }
    /**
     * Returns the user's default delivery address.
     *
     * @param userId the ID of the user
     * @return the default {@link DeliveryAddressDTO}
     * @throws UserNotFoundException if no default address is found
     */
    private DeliveryAddressDTO getDefaultAddress(Long userId) {
        return deliveryAddressService.getAllDeliveryAddresses(userId)
                .stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("No default address was found for the user."));
    }

    /**
     * Creates a {@link DeliveryTypeDTO} with estimated delivery date and final cost.
     *
     * @param deliveryType     the delivery type entity
     * @param currentDate      the current date
     * @param isFreeDelivery   whether delivery is free based on cart total
     * @param deliveryAddress  the user's delivery address
     * @return the constructed {@link DeliveryTypeDTO}
     */
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

    /**
     * Calculates the cart total including delivery cost for a user and delivery option.
     *
     * @param userId               the ID of the user
     * @param selectedDeliveryName the chosen delivery method
     * @return a {@link CartWithDeliveryDTO} containing the cart, delivery, and total amounts
     * @throws DeliveryTypeNotFoundException if the delivery type is invalid
     */
    public CartWithDeliveryDTO getCartWithDeliveryTotal(Long userId, DeliveryName selectedDeliveryName) {
        logger.info("Calculation of shopping cart total with delivery for user {}", userId);
        DeliveryType deliveryType = deliveryTypeRepository.findByDeliveryName(selectedDeliveryName)
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Invalid delivery type: " + selectedDeliveryName));

        BigDecimal cartTotal = getCartTotal(userId);
        BigDecimal deliveryCost = cartTotal.compareTo(new BigDecimal("50.0")) >= 0 ? BigDecimal.ZERO : deliveryType.getCost();

        return new CartWithDeliveryDTO(cartTotal, deliveryCost, cartTotal.add(deliveryCost));
    }

    /**
     * Retrieves the total cost of the active shopping cart for the given user.
     *
     * @param userId the ID of the user
     * @return the total amount of the active cart
     * @throws CartNotFoundException if no active cart is found
     */
    public BigDecimal getCartTotal(Long userId) {
        return cartRepository.findActiveCartByUser(userId)
                .map(Cart::calculateTotalAmount)
                .orElseThrow(() -> new CartNotFoundException("No active shopping cart found for user."));
    }

    /**
     * Adds a new delivery method to the user's account and updates the cart accordingly.
     *
     * @param request the delivery method creation request data
     * @return the saved {@link DeliveryMethodDTO}
     * @throws UserNotFoundException if the user does not exist
     * @throws DeliveryTypeNotFoundException if the delivery type does not exist
     * @throws CartNotFoundException if the cart is not found
     */
    @Transactional
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO request) {
        logger.info("Add delivery mode for user ID: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));

        DeliveryType deliveryType = deliveryTypeRepository.findById(request.getDeliveryTypeId())
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Delivery type not found with ID: " + request.getDeliveryTypeId()));

        DeliveryMethod deliveryMethod = new DeliveryMethod(deliveryType, user);

        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);

        Cart cart = cartRepository.findActiveCartByUser(user.getId())
                .orElseThrow(() -> new CartNotFoundException("No active cart found"));

        cart.setDeliveryMethod(savedDeliveryMethod);
        cart.setDeliveryType(savedDeliveryMethod.getDeliveryType());
        cartRepository.save(cart);

        return DeliveryMethodDTO.fromEntity(savedDeliveryMethod);
    }

    /**
     * Updates the delivery method for a given ID and updates the associated cart.
     *
     * @param deliveryMethodId the ID of the delivery method to update
     * @param request the request containing the new delivery type and user ID
     * @return the updated {@link DeliveryMethodDTO}
     * @throws DeliveryMethodNotFoundException if the delivery method does not exist
     * @throws DeliveryTypeNotFoundException if the new delivery type is invalid
     * @throws CartNotFoundException if no active cart is associated with the user
     */
    @Transactional
    public DeliveryMethodDTO updateDeliveryMethod(Long deliveryMethodId, UpdateDeliveryMethodRequestDTO request) {
        logger.info("Update delivery mode with ID: {}", deliveryMethodId);

        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery mode with ID " + deliveryMethodId + " not found."));

        DeliveryType deliveryType = deliveryTypeRepository.findById(request.getDeliveryTypeId())
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Delivery type with ID " + request.getDeliveryTypeId() + " not found."));

        deliveryMethod.setDeliveryType(deliveryType);

        DeliveryMethod updatedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);

        Cart cart = cartRepository.findActiveCartByUser(request.getUserId())
                .orElseThrow(() -> new CartNotFoundException("No active cart found."));

        cart.setDeliveryMethod(updatedDeliveryMethod);
        cart.setDeliveryType(updatedDeliveryMethod.getDeliveryType());
        cartRepository.save(cart);

        return DeliveryMethodDTO.fromEntity(updatedDeliveryMethod);
    }
}
