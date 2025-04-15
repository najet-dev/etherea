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
        return deliveryMethodRepository.findByUserId(userId)
                .stream()
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
        return cartRepository.findActiveCartByUser(userId)
                .map(Cart::calculateTotalAmount)
                .orElseThrow(() -> new CartNotFoundException("No active shopping cart found for user."));
    }
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
