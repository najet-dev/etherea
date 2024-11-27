package com.etherea.services;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.User;
import com.etherea.models.DeliveryAddress;
import com.etherea.models.DeliveryMethod;
import com.etherea.repositories.CartRepository;
import com.etherea.repositories.DeliveryAddressRepository;
import com.etherea.repositories.DeliveryMethodRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.factories.DeliveryMethodFactory;
import com.etherea.utils.DeliveryCostCalculator;
import com.etherea.utils.DeliveryDateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    /**
     * Retourne une liste des options de livraison disponibles avec leurs coûts et dates estimées.
     *
     * @param userId L'ID de l'utilisateur, nécessaire pour récupérer l'adresse par défaut.
     * @return Liste des options de livraison sous forme de DTOs.
     */
    public List<DeliveryMethodDTO> getDeliveryOptions(Long userId) {
        DeliveryAddressDTO defaultAddress = getDefaultAddress(userId);
        double cartTotal = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Panier introuvable pour l'utilisateur."))
                .calculateTotalAmount().doubleValue();

        boolean isFreeDelivery = cartTotal >= 50.0;
        LocalDate currentDate = LocalDate.now();

        // Créez les options de livraison
        return List.of(
                createDeliveryMethodDTO(DeliveryOption.HOME_STANDARD, currentDate.plusDays(7), isFreeDelivery ? 0.0 : 5.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryOption.HOME_EXPRESS, currentDate.plusDays(2), isFreeDelivery ? 0.0 : 10.0, defaultAddress, null),
                createDeliveryMethodDTO(DeliveryOption.PICKUP_POINT, currentDate.plusDays(8), isFreeDelivery ? 0.0 : 3.0, null, "")
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
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Aucune adresse par défaut n'a été trouvée pour l'utilisateur."));
    }

    /**
     * Crée une option de livraison de type `DeliveryMethodDTO`.
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
     * Calcule le total du panier avec le coût de la livraison.
     * La livraison est gratuite si le montant total du panier est supérieur ou égal à 50 €.
     *
     * @param cartTotal       Le montant total du panier (sans livraison).
     * @param selectedOption  L'option de livraison choisie.
     * @return Le montant total incluant les frais de livraison.
     */
    public double calculateTotal(double cartTotal, DeliveryOption selectedOption) {
        if (cartTotal < 0) {
            throw new IllegalArgumentException("Le montant du panier ne peut pas être négatif.");
        }

        // Utilisation de la méthode existante pour déterminer les frais de livraison
        double deliveryCost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, selectedOption);

        return cartTotal + deliveryCost;
    }

    /**
     * Ajoute une méthode de livraison à la commande.
     */
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getUserId() == null || requestDTO.getDeliveryOption() == null) {
            throw new IllegalArgumentException("Les données de la requête sont invalides.");
        }

        // Vérification de l'existence de l'utilisateur
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + requestDTO.getUserId()));

        // Récupération de l'adresse de l'utilisateur (pour HOME_STANDARD ou HOME_EXPRESS)
        DeliveryAddress userAddress = null;
        if (requestDTO.getDeliveryOption() == DeliveryOption.HOME_STANDARD || requestDTO.getDeliveryOption() == DeliveryOption.HOME_EXPRESS) {
            if (requestDTO.getAddressId() == null) {
                throw new DeliveryAddressNotFoundException("L'ID de l'adresse est requis pour la livraison à domicile.");
            }
            userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(requestDTO.getUserId())
                    .orElseThrow(() -> new DeliveryAddressNotFoundException("Aucune adresse trouvée pour l'utilisateur avec l'ID : " + requestDTO.getUserId()));
        }

        // Création de la méthode de livraison via la factory
        DeliveryMethod deliveryMethod = DeliveryMethodFactory.createDeliveryMethod(
                requestDTO.getDeliveryOption(),
                userAddress, // Utiliser l'adresse de l'utilisateur ici si c'est une livraison à domicile
                requestDTO.getPickupPointName(),
                requestDTO.getPickupPointAddress(),
                requestDTO.getPickupPointLatitude(),
                requestDTO.getPickupPointLongitude(),
                user
        );

        // Calcul des coûts et des dates
        double orderAmount = requestDTO.getOrderAmount();
        LocalDate startDate = LocalDate.now();

        // Calcul des frais et de la date de livraison selon le mode de livraison
        if (requestDTO.getDeliveryOption() == DeliveryOption.PICKUP_POINT) {
            // Point relais
            deliveryMethod.setDeliveryCost(DeliveryCostCalculator.calculateDeliveryCost(orderAmount, DeliveryOption.PICKUP_POINT));
            deliveryMethod.setExpectedDeliveryDate(
                    deliveryDateCalculator.calculateDeliveryDate(startDate, 8) // Supposons 8 jours pour un point relais
            );
        } else {
            // Livraison à domicile (standard ou express)
            deliveryMethod.setDeliveryCost(DeliveryCostCalculator.calculateDeliveryCost(orderAmount, requestDTO.getDeliveryOption()));
            deliveryMethod.setExpectedDeliveryDate(
                    deliveryDateCalculator.calculateDeliveryDate(startDate, deliveryMethod.calculateDeliveryTime())
            );
        }

        // Sauvegarde en base de données
        DeliveryMethod savedMethod = deliveryMethodRepository.save(deliveryMethod);

        // Retour du DTO avec les informations nécessaires
        return DeliveryMethodDTO.fromDeliveryMethod(
                savedMethod,
                userAddress, // Passer l'adresse utilisateur pour les livraisons à domicile
                startDate,
                orderAmount,
                deliveryDateCalculator
        );
    }

}
