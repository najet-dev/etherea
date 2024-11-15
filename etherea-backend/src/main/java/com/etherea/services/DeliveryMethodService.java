package com.etherea.services;

import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.*;
import com.etherea.utils.DeliveryDateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DeliveryMethodService {

    // Dépendances injectées
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private DeliveryDateCalculator deliveryDateCalculator;

    @Autowired
    private HomeStandardDeliveryRepository homeStandardDeliveryRepository;

    @Autowired
    private HomeExpressDeliveryRepository homeExpressDeliveryRepository;

    @Autowired
    private PickupPointDeliveryRepository pickupPointDeliveryRepository;

    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;

    @Autowired
    private CartRepository cartRepository;

    public DeliveryMethodDTO getDeliveryMethod(Long userId, Long deliveryMethodId) {
        // Vérifier l'utilisateur
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + userId));

        // Récupérer le mode de livraison
        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Mode de livraison introuvable avec l'ID : " + deliveryMethodId));

        // Déterminer l'adresse de livraison si applicable
        DeliveryAddress deliveryAddress = deliveryMethod instanceof HomeStandardDelivery ?
                ((HomeStandardDelivery) deliveryMethod).getDeliveryAddress() :
                deliveryMethod instanceof HomeExpressDelivery ?
                        ((HomeExpressDelivery) deliveryMethod).getDeliveryAddress() :
                        null;

        // Récupérer le panier
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Panier introuvable pour l'utilisateur avec l'ID : " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide pour l'utilisateur avec l'ID : " + userId);
        }

        // Calculer le coût de livraison
        double deliveryCost = calculateDeliveryCost(cart.calculateTotalAmount().doubleValue(), deliveryMethod);

        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, deliveryAddress, LocalDate.now(), deliveryCost, deliveryDateCalculator);
    }

    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO request) {
        // Vérification de l'utilisateur
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + request.getUserId()));

        // Récupération de l'adresse
        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Aucune adresse trouvée pour l'utilisateur avec l'ID : " + request.getUserId()));

        // Récupération du panier
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Panier introuvable pour l'utilisateur avec l'ID : " + request.getUserId()));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide pour l'utilisateur avec l'ID : " + request.getUserId());
        }

        // Calcul du coût de livraison en fonction de l'option
        DeliveryMethod deliveryMethod;
        double deliveryCost;
        LocalDate startDate = LocalDate.now();

        switch (request.getDeliveryOption()) {
            case HOME_STANDARD:
                HomeStandardDelivery standardDelivery = new HomeStandardDelivery();
                standardDelivery.setDeliveryAddress(userAddress);
                deliveryMethod = homeStandardDeliveryRepository.save(standardDelivery);
                deliveryCost = calculateDeliveryCost(cart.calculateTotalAmount().doubleValue(), standardDelivery);
                break;

            case HOME_EXPRESS:
                HomeExpressDelivery expressDelivery = new HomeExpressDelivery();
                expressDelivery.setDeliveryAddress(userAddress);
                deliveryMethod = homeExpressDeliveryRepository.save(expressDelivery);
                deliveryCost = calculateDeliveryCost(cart.calculateTotalAmount().doubleValue(), expressDelivery);
                break;

            case PICKUP_POINT:
                if (request.getPickupPointName() == null || request.getPickupPointAddress() == null ||
                        request.getPickupPointLatitude() == null || request.getPickupPointLongitude() == null) {
                    throw new IllegalArgumentException("Informations manquantes pour la livraison au point relais");
                }

                PickupPointDelivery pickupPointDelivery = new PickupPointDelivery(
                        request.getPickupPointName(),
                        request.getPickupPointAddress(),
                        request.getPickupPointLatitude(),
                        request.getPickupPointLongitude(),
                        user
                );
                deliveryMethod = pickupPointDeliveryRepository.save(pickupPointDelivery);
                deliveryCost = calculateDeliveryCost(cart.calculateTotalAmount().doubleValue(), pickupPointDelivery);
                break;

            default:
                throw new IllegalArgumentException("Option de livraison invalide.");
        }

        // Associer le mode de livraison au panier
        cart.setDeliveryMethod(deliveryMethod);
        cartRepository.save(cart);

        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, userAddress, startDate, deliveryCost, deliveryDateCalculator);
    }

    private double calculateDeliveryCost(double cartTotalAmount, DeliveryMethod deliveryMethod) {
        System.out.println("Montant total du panier : " + cartTotalAmount);
        System.out.println("Type de livraison : " + deliveryMethod.getClass().getSimpleName());

        if (cartTotalAmount >= 50.0) {
            System.out.println("Livraison gratuite appliquée.");
            return 0.0;
        }
        if (deliveryMethod instanceof HomeStandardDelivery) {
            return 5.0;
        }
        if (deliveryMethod instanceof HomeExpressDelivery) {
            return 8.0;
        }
        if (deliveryMethod instanceof PickupPointDelivery) {
            return 3.0;
        }
        throw new IllegalArgumentException("Type de livraison inconnu.");
    }


}