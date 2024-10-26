package com.etherea.services;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.repositories.DeliveryAddressRepository;
import com.etherea.repositories.PickupPointRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class DeliveryMethodService {
    public final PickupPointRepository pickupPointRepository;
    @Autowired
    public DeliveryMethodService(PickupPointRepository pickupPointRepository) {
        this.pickupPointRepository = pickupPointRepository;
    }

    // Calcul du coût de la livraison en fonction du type de livraison
    public double calculateCost(DeliveryOption deliveryOption, double orderAmount) {
        double cost;
        switch (deliveryOption) {
            case HOME_STANDARD -> cost = 5.0;
            case HOME_EXPRESS -> cost = 10.0;
            case PICKUP_POINT -> cost = 3.0;
            default -> throw new IllegalArgumentException("Unknown delivery option");
        }

        // livraison gratuite au-delà de 50€
        if (orderAmount >= 50) {
            cost = 0.0;
        }
        return cost;
    }

    // Calcul de la date de livraison en fonction du type de livraison
    public LocalDate calculateExpectedDeliveryDate(DeliveryOption deliveryOption) {
        LocalDate deliveryDate;
        switch (deliveryOption) {
            case HOME_STANDARD -> deliveryDate = LocalDate.now().plusDays(5);
            case HOME_EXPRESS -> deliveryDate = LocalDate.now().plusDays(2);
            case PICKUP_POINT -> deliveryDate = LocalDate.now().plusDays(7);
            default -> throw new IllegalArgumentException("Unknown delivery option");
        }
        return deliveryDate;
    }

    // Configuration complète d'une méthode de livraison
    public DeliveryMethod configureDeliveryMethod(DeliveryMethod deliveryMethod, double orderAmount) {
        // Calcul du coût et de la date de livraison
        deliveryMethod.setCost(calculateCost(deliveryMethod.getDeliveryOption(), orderAmount));
        deliveryMethod.setExpectedDeliveryDate(calculateExpectedDeliveryDate(deliveryMethod.getDeliveryOption()));
        return deliveryMethod;
    }
}
