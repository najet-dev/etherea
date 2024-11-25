package com.etherea.services;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.DeliveryMethod;
import com.etherea.repositories.CartRepository;
import com.etherea.repositories.DeliveryMethodRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.utils.DeliveryCostCalculator;
import com.etherea.utils.DeliveryDateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.etherea.utils.DeliveryCostCalculator.calculateDeliveryCost;

@Service
public class DeliveryMethodService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private DeliveryDateCalculator deliveryDateCalculator;

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    /**
     * Retourne une liste des options de livraison disponibles avec leurs coûts et dates estimées.
     *
     * @param userId L'ID de l'utilisateur, nécessaire pour récupérer l'adresse par défaut.
     * @return Liste des options de livraison sous forme de DTOs.
     */
    public List<DeliveryMethodDTO> getDeliveryOptions(Long userId) {
        // Récupérer l'adresse par défaut de l'utilisateur
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);

        // Date actuelle
        LocalDate currentDate = LocalDate.now();

        // Construire les options de livraison
        return List.of(
                createDeliveryMethodDTO(DeliveryOption.HOME_STANDARD, currentDate.plusDays(7), 5.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryOption.HOME_EXPRESS, currentDate.plusDays(2), 10.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryOption.PICKUP_POINT, currentDate.plusDays(8), 3.0, null, "")
        );
    }

    /**
     * Récupère l'adresse par défaut de l'utilisateur.
     *
     * @param userId L'ID de l'utilisateur.
     * @return L'adresse par défaut.
     * @throws UserNotFoundException Si l'utilisateur n'a pas d'adresse par défaut.
     */
    private DeliveryAddressDTO getDefaultAddress(Long userId) {
        return deliveryAddressService.getAllDeliveryAddresses(userId)
                .stream()
                .filter(DeliveryAddressDTO::isDefault) // Trouver l'adresse par défaut
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Aucune adresse par défaut n'a été trouvée pour l'utilisateur."));
    }

    /**
     * Crée une option de livraison de type `DeliveryMethodDTO`.
     *
     * @param option           Le type d'option de livraison.
     * @param deliveryDate     La date de livraison estimée.
     * @param cost             Le coût de livraison.
     * @param deliveryAddress  L'adresse de livraison, peut être `null` pour un point relais.
     * @param pickupPointName  Le nom du point relais, peut être `null` pour une livraison à domicile.
     * @return L'objet `DeliveryMethodDTO` créé.
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
    public double calculateTotal(double cartTotal, DeliveryOption selectedOption) {
        // Calculer le coût de livraison pour l'option choisie
        double deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, selectedOption);
        // Retourner le total : panier + livraison
        return cartTotal + deliveryCost;
    }

}
