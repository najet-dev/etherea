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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryMethodService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    private final DeliveryDateCalculator deliveryDateCalculator;

    private static final Logger logger = LoggerFactory.getLogger(DeliveryMethodService.class);

    public DeliveryMethodService(HolidayProvider holidayProvider) {
        this.deliveryDateCalculator = new DeliveryDateCalculator(holidayProvider);
    }

    /**
     * Retourne les options de livraison disponibles avec coûts et dates estimées.
     */
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

    private DeliveryMethodDTO createDeliveryMethodDTO(
            DeliveryType type, LocalDate deliveryDate, double cost,
            DeliveryAddressDTO deliveryAddress, String pickupPointName) {

        return new DeliveryMethodDTO(
                null,
                type,
                deliveryDate,
                BigDecimal.valueOf(cost),
                deliveryAddress,
                pickupPointName != null ? new PickupPointDetailsDTO(pickupPointName, " ", 0.0, 0.0) : null
        );
    }

    /**
     * Calcule le total du panier incluant les frais de livraison.
     */
    public CartWithDeliveryDTO getCartWithDeliveryTotal(Long userId, DeliveryType selectedType) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Panier introuvable pour l'utilisateur."));

        BigDecimal cartTotal = cart.calculateTotalAmount();

        DeliveryMethod deliveryMethod = deliveryMethodRepository.findByType(selectedType)
                .orElseThrow(() -> new IllegalArgumentException("Type de livraison invalide"));

        BigDecimal deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, deliveryMethod);
        BigDecimal total = cartTotal.add(deliveryCost);

        cart.calculateFinalTotal();
        cartRepository.save(cart);

        return new CartWithDeliveryDTO(cartTotal, deliveryCost, total);
    }

    /**
     * Retourne le montant total du panier pour un utilisateur donné.
     */
    public double getCartTotal(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Panier introuvable pour l'utilisateur."))
                .calculateTotalAmount().doubleValue();
    }

    /**
     * Ajoute une méthode de livraison à la commande de l'utilisateur.
     */
    @Transactional
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO requestDTO) {
        logger.info("Ajout d'une méthode de livraison : {}", requestDTO);

        if (requestDTO == null || requestDTO.getUserId() == null || requestDTO.getDeliveryType() == null) {
            throw new IllegalArgumentException("Les informations de livraison sont incomplètes.");
        }

        // Récupération de l'utilisateur
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec ID : " + requestDTO.getUserId()));

        DeliveryAddress deliveryAddress = null;
        PickupPointDetails pickupPointDetails = null;

        // Vérification du type de livraison
        if (requestDTO.getDeliveryType() == DeliveryType.HOME_STANDARD || requestDTO.getDeliveryType() == DeliveryType.HOME_EXPRESS) {
            if (requestDTO.getAddressId() == null) {
                throw new DeliveryAddressNotFoundException("Adresse requise pour la livraison à domicile.");
            }
            deliveryAddress = deliveryAddressRepository.findById(requestDTO.getAddressId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Adresse introuvable avec ID : " + requestDTO.getAddressId()));
        } else if (requestDTO.getDeliveryType() == DeliveryType.PICKUP_POINT) {
            if (requestDTO.getPickupPointName() == null || requestDTO.getPickupPointAddress() == null) {
                throw new IllegalArgumentException("Informations du point relais incomplètes.");
            }
            pickupPointDetails = new PickupPointDetails(
                    requestDTO.getPickupPointName(),
                    requestDTO.getPickupPointAddress(),
                    requestDTO.getPickupPointLatitude(),
                    requestDTO.getPickupPointLongitude()
            );
        }

        // Récupération du montant de la commande et calcul du coût de livraison
        BigDecimal orderAmount = BigDecimal.valueOf(requestDTO.getOrderAmount());
        LocalDate startDate = LocalDate.now();

        // Création d'un objet temporaire pour la méthode de livraison
        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setType(requestDTO.getDeliveryType());

       // Détermination du nombre de jours de livraison
        int deliveryDays = switch (requestDTO.getDeliveryType()) {
            case HOME_EXPRESS -> 2;
            case HOME_STANDARD -> 7;
            case PICKUP_POINT -> 8;
            default -> throw new IllegalArgumentException("Type de livraison non reconnu.");
        };

        // Configuration de la méthode de livraison
        deliveryMethod.setDeliveryDays(deliveryDays);
        deliveryMethod.setUser(user);
        deliveryMethod.setDeliveryAddress(deliveryAddress);
        deliveryMethod.setPickupPointDetails(pickupPointDetails);

        // Calcul du coût de livraison en fonction du montant du panier et de la gratuité
        BigDecimal deliveryCost = BigDecimal.ZERO;
        if (orderAmount.compareTo(BigDecimal.valueOf(50)) < 0) {
            // Si le montant de la commande est inférieur à 50€, appliquer le coût de livraison standard
            deliveryCost = switch (requestDTO.getDeliveryType()) {
                case HOME_STANDARD -> BigDecimal.valueOf(5);
                case HOME_EXPRESS -> BigDecimal.valueOf(10);
                case PICKUP_POINT -> BigDecimal.valueOf(3);
                default -> throw new IllegalArgumentException("Type de livraison non reconnu.");
            };
        }
        deliveryMethod.setCost(deliveryCost);

        // Calcul de la date de livraison
        LocalDate expectedDeliveryDate = deliveryDateCalculator.calculateDeliveryDate(startDate, deliveryMethod);

        // Sauvegarde de la méthode de livraison
        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);

        // Mise à jour du panier avec la méthode de livraison
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Panier introuvable pour l'utilisateur."));
        cart.setDeliveryMethod(savedDeliveryMethod);
        cartRepository.save(cart);

        // Retourner le DTO avec les informations de la méthode de livraison
        return DeliveryMethodDTO.fromDeliveryMethod(savedDeliveryMethod, startDate, orderAmount, deliveryDateCalculator);
    }
}
