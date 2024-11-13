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

import java.time.LocalDate;

@Service
public class DeliveryMethodService {

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
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new IllegalArgumentException("No delivery method found with ID: " + deliveryMethodId));

        DeliveryAddress deliveryAddress = deliveryMethod instanceof HomeStandardDelivery ?
                ((HomeStandardDelivery) deliveryMethod).getDeliveryAddress() :
                deliveryMethod instanceof HomeExpressDelivery ?
                        ((HomeExpressDelivery) deliveryMethod).getDeliveryAddress() :
                        null;

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Panier introuvable pour l'utilisateur avec l'ID : " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide pour l'utilisateur avec l'ID : " + userId);
        }

        double deliveryCost = calculateDeliveryCost(cart.calculateTotalAmount().doubleValue());

        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, deliveryAddress, LocalDate.now(), deliveryCost, deliveryDateCalculator);
    }

    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + request.getUserId()));

        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Aucune adresse trouvée pour l'utilisateur avec l'ID : " + request.getUserId()));

        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Panier introuvable pour l'utilisateur avec l'ID : " + request.getUserId()));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide pour l'utilisateur avec l'ID : " + request.getUserId());
        }

        double deliveryCost = calculateDeliveryCost(cart.calculateTotalAmount().doubleValue());

        DeliveryMethod deliveryMethod;
        LocalDate startDate = LocalDate.now();

        switch (request.getDeliveryOption()) {
            case HOME_STANDARD:
                HomeStandardDelivery standardDelivery = new HomeStandardDelivery();
                standardDelivery.setDeliveryAddress(userAddress);
                deliveryMethod = homeStandardDeliveryRepository.save(standardDelivery);
                break;

            case HOME_EXPRESS:
                HomeExpressDelivery expressDelivery = new HomeExpressDelivery();
                expressDelivery.setDeliveryAddress(userAddress);
                deliveryMethod = homeExpressDeliveryRepository.save(expressDelivery);
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
                break;

            default:
                throw new IllegalArgumentException("Option de livraison invalide.");
        }

        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, userAddress, startDate, deliveryCost, deliveryDateCalculator);
    }
    private double calculateDeliveryCost(double cartTotalAmount) {
        return cartTotalAmount >= 50 ? 0 : (cartTotalAmount * 0.05);
    }
}
