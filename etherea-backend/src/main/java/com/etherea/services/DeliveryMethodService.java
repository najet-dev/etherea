package com.etherea.services;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.CartWithDeliveryDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.enums.DeliveryType;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.Cart;
import com.etherea.repositories.CartRepository;
import com.etherea.utils.DeliveryCostCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryMethodService {

    private final CartRepository cartRepository;
    private final DeliveryAddressService deliveryAddressService;

    public DeliveryMethodService(CartRepository cartRepository, DeliveryAddressService deliveryAddressService) {
        this.cartRepository = cartRepository;
        this.deliveryAddressService = deliveryAddressService;
    }

    /**
     * Récupère les options de livraison disponibles en fonction du panier de l'utilisateur.
     *
     * @param userId L'ID de l'utilisateur pour récupérer son adresse par défaut.
     * @return Liste des options de livraison sous forme de DTOs.
     */
    public List<DeliveryMethodDTO> getDeliveryOptions(Long userId) {
        // Récupération de l'adresse par défaut pour l'utilisateur
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);

        // Récupération du panier de l'utilisateur
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Panier non trouvé pour l'utilisateur."));

        double cartTotal = cart.calculateTotalAmount().doubleValue();
        boolean isFreeDelivery = cartTotal >= 50.0;
        LocalDate currentDate = LocalDate.now();

        // Création des différentes options de livraison
        return List.of(
                createDeliveryMethodDTO(DeliveryType.HOME_STANDARD, currentDate.plusDays(7), isFreeDelivery ? 0.0 : 5.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryType.HOME_EXPRESS, currentDate.plusDays(2), isFreeDelivery ? 0.0 : 10.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryType.PICKUP_POINT, currentDate.plusDays(8), isFreeDelivery ? 0.0 : 3.0, null, "Point Relais XYZ")
        );
    }

    /**
     * Récupère l'adresse de livraison par défaut de l'utilisateur.
     *
     * @param userId L'ID de l'utilisateur.
     * @return L'adresse par défaut de l'utilisateur.
     * @throws UserNotFoundException Si aucune adresse par défaut n'est trouvée.
     */
    private DeliveryAddressDTO getDefaultAddress(Long userId) {
        return deliveryAddressService.getAllDeliveryAddresses(userId)
                .stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new DeliveryAddressNotFoundException("Aucune adresse par défaut trouvée pour cet utilisateur."));
    }

    /**
     * Création d'une option de livraison sous forme de DTO.
     *
     * @param option Le type de livraison.
     * @param deliveryDate La date estimée de livraison.
     * @param cost Le coût de la livraison.
     * @param deliveryAddress L'adresse de livraison (si applicable).
     * @param pickupPointName Le nom du point relais (si applicable).
     * @return Une instance de `DeliveryMethodDTO`.
     */
    private DeliveryMethodDTO createDeliveryMethodDTO(
            DeliveryType option, LocalDate deliveryDate, Double cost,
            DeliveryAddressDTO deliveryAddress, String pickupPointName) {

        return new DeliveryMethodDTO.Builder()
                .setDeliveryOption(option)
                .setExpectedDeliveryDate(deliveryDate)
                .setCost(cost)
                .setDeliveryAddress(deliveryAddress)
                .setPickupPointName(pickupPointName)
                .setPickupPointAddress(option == DeliveryType.PICKUP_POINT ? "123 Rue du Point Relais" : null)
                .setPickupPointLatitude(option == DeliveryType.PICKUP_POINT ? 48.8566 : null) // Paris (exemple)
                .setPickupPointLongitude(option == DeliveryType.PICKUP_POINT ? 2.3522 : null)
                .build();
    }

    /**
     * Récupère le montant total du panier avec les frais de livraison selon l'option choisie.
     *
     * @param userId L'ID de l'utilisateur.
     * @param selectedOption L'option de livraison choisie.
     * @return Un `CartWithDeliveryDTO` contenant le total du panier, le coût de livraison et le total final.
     */
    public CartWithDeliveryDTO getCartWithDeliveryTotal(Long userId, DeliveryOption selectedOption) {
        // Récupération du panier
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Panier non trouvé pour l'utilisateur."));

        // Calcul du total du panier (hors livraison)
        double cartTotal = cart.calculateTotalAmount().doubleValue();

        // Calcul des frais de livraison
        double deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, selectedOption);

        // Calcul du total final avec livraison
        double total = cartTotal + deliveryCost;

        // Mise à jour du total final du panier en base de données
        cart.calculateFinalTotal();
        cartRepository.save(cart);

        // Retourne un DTO avec les totaux mis à jour
        return new CartWithDeliveryDTO(cartTotal, deliveryCost, total);
    }

    /**
     * Récupère le total du panier pour un utilisateur donné.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Le montant total du panier.
     */
    public double getCartTotal(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Panier non trouvé pour l'utilisateur."));
        return cart.calculateTotalAmount().doubleValue();
    }
}
