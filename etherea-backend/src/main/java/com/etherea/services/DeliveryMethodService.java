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
import com.etherea.utils.DeliveryCostCalculator;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public List<DeliveryMethodDTO> getAllDeliveryMethods(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + userId));
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Panier introuvable pour l'utilisateur avec l'ID : " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide pour l'utilisateur avec l'ID : " + userId);
        }

        double cartTotal = cart.calculateTotalAmount().doubleValue();
        LocalDate startDate = LocalDate.now();

        List<DeliveryMethodDTO> deliveryMethods = new ArrayList<>();

        for (DeliveryOption option : DeliveryOption.values()) {
            double cost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, option); // Utilisation de la classe utilitaire
            LocalDate deliveryDate = deliveryDateCalculator.calculateDeliveryDate(startDate, option);
            deliveryMethods.add(new DeliveryMethodDTO(option, cost, deliveryDate));
        }

        return deliveryMethods;
    }
    public DeliveryMethodDTO getDeliveryMethod(Long userId, Long deliveryMethodId) {
        // User existence check
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + userId));

        // Retrieve delivery method
        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Mode de livraison introuvable avec l'ID : " + deliveryMethodId));

        // Retrieve delivery address if applicable
        DeliveryAddress deliveryAddress = (deliveryMethod instanceof HomeStandardDelivery)
                ? ((HomeStandardDelivery) deliveryMethod).getDeliveryAddress()
                : (deliveryMethod instanceof HomeExpressDelivery)
                ? ((HomeExpressDelivery) deliveryMethod).getDeliveryAddress()
                : null;

        // retrieve cart shopping
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Panier introuvable pour l'utilisateur avec l'ID : " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide pour l'utilisateur avec l'ID : " + userId);
        }

        // Delivery cost calculation
        double cartTotal = cart.calculateTotalAmount().doubleValue();
        double deliveryCost = deliveryMethod.calculateCost(cartTotal);

        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, deliveryAddress, LocalDate.now(), cartTotal, deliveryDateCalculator);
    }

    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO request) {
        // User existence check
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User ID not found with ID : " + request.getUserId()));

        // Retrieve user address
        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No address found for user with ID : " + request.getUserId()));

        // retrieve cart shopping
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found for user with ID : " + request.getUserId()));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("The shopping cart is empty for the user with the ID: " + request.getUserId());
        }

        // Initialization of variables
        DeliveryMethod deliveryMethod;
        LocalDate startDate = LocalDate.now();
        double cartTotal = cart.calculateTotalAmount().doubleValue();

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
                    throw new IllegalArgumentException("Missing information for relay point delivery");
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
                throw new IllegalArgumentException("Delivery option invalid.");
        }

        // Link delivery method to shopping cart
        cart.setDeliveryMethod(deliveryMethod);
        cartRepository.save(cart);

        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, userAddress, startDate, cartTotal, deliveryDateCalculator);
    }

    // Method for calculating delivery costs
    private double calculateDeliveryCost(double cartTotalAmount, DeliveryMethod deliveryMethod) {
        return deliveryMethod.calculateCost(cartTotalAmount);
    }
}
