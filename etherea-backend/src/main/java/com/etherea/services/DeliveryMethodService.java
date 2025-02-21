package com.etherea.services;

import com.etherea.dtos.*;
import com.etherea.enums.DeliveryType;
import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.*;
import com.etherea.utils.DeliveryCostCalculator;
import com.etherea.utils.DeliveryDateCalculator;
import com.etherea.utils.HolidayProvider;
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
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final CartRepository cartRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final DeliveryAddressService deliveryAddressService;
    public final DeliveryDateCalculator deliveryDateCalculator;

    private static final Logger logger = LoggerFactory.getLogger(DeliveryMethodService.class);

    public DeliveryMethodService(UserRepository userRepository, DeliveryMethodRepository deliveryMethodRepository,
                                 CartRepository cartRepository, DeliveryAddressRepository deliveryAddressRepository,
                                 DeliveryAddressService deliveryAddressService, HolidayProvider holidayProvider) {
        this.userRepository = userRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.cartRepository = cartRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.deliveryAddressService = deliveryAddressService;
        this.deliveryDateCalculator = new DeliveryDateCalculator(holidayProvider);
    }

    public List<DeliveryMethodDTO> getDeliveryOptions(Long userId) {
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);
        double cartTotal = getCartTotal(userId);
        boolean isFreeDelivery = cartTotal >= 50.0;
        LocalDate currentDate = LocalDate.now();

        return List.of(
                createDeliveryMethodDTO(DeliveryType.HOME_STANDARD, currentDate.plusDays(7), isFreeDelivery ? 0.0 : 5.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryType.HOME_EXPRESS, currentDate.plusDays(2), isFreeDelivery ? 0.0 : 10.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryType.PICKUP_POINT, currentDate.plusDays(8), isFreeDelivery ? 0.0 : 3.0, null, "Point Relais")
        );
    }

    private DeliveryAddressDTO getDefaultAddress(Long userId) {
        return deliveryAddressService.getAllDeliveryAddresses(userId)
                .stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Aucune adresse par défaut trouvée pour l'utilisateur."));
    }

    private DeliveryMethodDTO createDeliveryMethodDTO(DeliveryType type, LocalDate deliveryDate, double cost,
                                                      DeliveryAddressDTO deliveryAddress, String pickupPointName) {
        return new DeliveryMethodDTO(
                null, type, deliveryDate, BigDecimal.valueOf(cost), deliveryAddress,
                pickupPointName != null ? new PickupPointDetailsDTO(pickupPointName, "", 0.0, 0.0) : null
        );
    }

    public CartWithDeliveryDTO getCartWithDeliveryTotal(Long userId, DeliveryType selectedType) {
        logger.info("Calcul du total du panier avec la livraison pour l'utilisateur {}", userId);
        DeliveryMethod deliveryMethod = deliveryMethodRepository.findByType(selectedType)
                .orElseThrow(() -> new IllegalArgumentException("Type de livraison invalide : " + selectedType));

        BigDecimal cartTotal = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Panier introuvable pour l'utilisateur."))
                .calculateTotalAmount();

        BigDecimal deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, deliveryMethod);
        return new CartWithDeliveryDTO(cartTotal, deliveryCost, cartTotal.add(deliveryCost));
    }

    public double getCartTotal(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Panier introuvable pour l'utilisateur."))
                .calculateTotalAmount().doubleValue();
    }

    @Transactional
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO requestDTO) {
        logger.info("Ajout d'une méthode de livraison : {}", requestDTO);

        if (requestDTO == null || requestDTO.getUserId() == null || requestDTO.getDeliveryType() == null) {
            throw new IllegalArgumentException("Les informations de livraison sont incomplètes.");
        }

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable."));

        DeliveryAddress deliveryAddress = null;
        PickupPointDetails pickupPointDetails = null;

        if (requestDTO.getDeliveryType() == DeliveryType.HOME_STANDARD || requestDTO.getDeliveryType() == DeliveryType.HOME_EXPRESS) {
            if (requestDTO.getAddressId() == null) {
                throw new DeliveryAddressNotFoundException("Adresse requise pour la livraison à domicile.");
            }
            deliveryAddress = deliveryAddressRepository.findById(requestDTO.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Adresse introuvable."));
        } else if (requestDTO.getDeliveryType() == DeliveryType.PICKUP_POINT) {
            if (requestDTO.getPickupPointName() == null || requestDTO.getPickupPointAddress() == null) {
                throw new IllegalArgumentException("Informations du point relais incomplètes.");
            }
            pickupPointDetails = new PickupPointDetails(
                    requestDTO.getPickupPointName(), requestDTO.getPickupPointAddress(),
                    requestDTO.getPickupPointLatitude(), requestDTO.getPickupPointLongitude()
            );
        }

        BigDecimal orderAmount = BigDecimal.valueOf(requestDTO.getOrderAmount());
        int deliveryDays = switch (requestDTO.getDeliveryType()) {
            case HOME_EXPRESS -> 2;
            case HOME_STANDARD -> 7;
            case PICKUP_POINT -> 8;
        };

        BigDecimal deliveryCost = orderAmount.compareTo(BigDecimal.valueOf(50)) < 0 ?
                switch (requestDTO.getDeliveryType()) {
                    case HOME_STANDARD -> BigDecimal.valueOf(5);
                    case HOME_EXPRESS -> BigDecimal.valueOf(10);
                    case PICKUP_POINT -> BigDecimal.valueOf(3);
                } : BigDecimal.ZERO;

        DeliveryMethod deliveryMethod = new DeliveryMethod(requestDTO.getDeliveryType(), deliveryDays, deliveryCost, user, deliveryAddress, pickupPointDetails);
        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Panier introuvable."));
        cart.setDeliveryMethod(savedDeliveryMethod);
        cartRepository.save(cart);

        return DeliveryMethodDTO.fromDeliveryMethod(savedDeliveryMethod, LocalDate.now(), orderAmount, deliveryDateCalculator);
    }
}
