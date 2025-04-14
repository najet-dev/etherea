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
import java.util.Optional;

@Service
public class DeliveryMethodService {
    private final UserRepository userRepository;
    private final DeliveryTypeRepository deliveryTypeRepository;
    private final CartRepository cartRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
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
        this.pickupPointDetailsRepository = pickupPointDetailsRepository;
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

        BigDecimal finalDeliveryCost = request.getOrderAmount().compareTo(new BigDecimal("50.0")) >= 0
                ? BigDecimal.ZERO
                : deliveryType.getCost();

        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setUser(user);
        deliveryMethod.setDeliveryType(deliveryType);

        if (DeliveryName.PICKUP_POINT.equals(deliveryType.getDeliveryName())) {
            // Logique pour les points de retrait (pickup)
            if (request.getPickupPointName() == null || request.getPickupPointAddress() == null) {
                throw new DeliveryAddressNotFoundException("The name and address of the collection point are required.");
            }
            PickupPointDetails pickupPoint = new PickupPointDetails(
                    request.getPickupPointName(),
                    request.getPickupPointAddress(),
                    request.getPickupPointLatitude(),
                    request.getPickupPointLongitude()
            );

            pickupPoint = pickupPointDetailsRepository.save(pickupPoint);
            deliveryMethod.setPickupPointDetails(pickupPoint);
        } else if (request.getAddressId() != null) {
            // Vérification de l'appartenance de l'adresse à l'utilisateur
            DeliveryAddress deliveryAddress = deliveryAddressRepository.findByIdWithUser(request.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found with ID: " + request.getAddressId()));

            if (!deliveryAddress.getUser().getId().equals(user.getId())) {
                throw new DeliveryAddressNotFoundException("This address does not belong to the current user.");
            }

            deliveryMethod.setDeliveryAddress(deliveryAddress);
        } else {
            throw new DeliveryAddressNotFoundException("A delivery address or collection point must be specified.");
        }

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

        DeliveryMethod existingDeliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery mode with ID " + deliveryMethodId + " not found."));

        DeliveryType deliveryType = deliveryTypeRepository.findById(request.getDeliveryTypeId())
                .orElseThrow(() -> new DeliveryTypeNotFoundException("Delivery type with ID " + request.getDeliveryTypeId() + " not found."));

        existingDeliveryMethod.setDeliveryType(deliveryType);

        if (DeliveryName.PICKUP_POINT.equals(deliveryType.getDeliveryName())) {
            PickupPointDetails pickupPoint = new PickupPointDetails(
                    request.getPickupPointName(),
                    request.getPickupPointAddress(),
                    request.getPickupPointLatitude(),
                    request.getPickupPointLongitude()
            );
            existingDeliveryMethod.setPickupPointDetails(pickupPoint);
            existingDeliveryMethod.setDeliveryAddress(null);
        } else if (request.getAddressId() != null) {
            // Vérification de l'appartenance de l'adresse
            DeliveryAddress deliveryAddress = deliveryAddressRepository.findByIdWithUser(request.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found with ID: " + request.getAddressId()));

            if (!deliveryAddress.getUser().getId().equals(existingDeliveryMethod.getUser().getId())) {
                throw new DeliveryAddressNotFoundException("This address does not belong to the current user.");
            }

            existingDeliveryMethod.setDeliveryAddress(deliveryAddress);
            existingDeliveryMethod.setPickupPointDetails(null);
        }

        DeliveryMethod updatedDeliveryMethod = deliveryMethodRepository.save(existingDeliveryMethod);

        Cart cart = cartRepository.findActiveCartByUser(request.getUserId())
                .orElseThrow(() -> new CartNotFoundException("No active cart found."));

        cart.setDeliveryMethod(updatedDeliveryMethod);
        cart.setDeliveryType(updatedDeliveryMethod.getDeliveryType());
        cartRepository.save(cart);

        return DeliveryMethodDTO.fromEntity(updatedDeliveryMethod);
    }

}
