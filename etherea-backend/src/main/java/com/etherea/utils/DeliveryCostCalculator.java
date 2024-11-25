package com.etherea.utils;

import com.etherea.enums.DeliveryOption;

public class DeliveryCostCalculator {

    private static final double FREE_SHIPPING_THRESHOLD = 50.0;

    private DeliveryCostCalculator() {
        // Constructeur privé pour empêcher l'instanciation
    }

    public static double calculateDeliveryCost(double cartTotal, DeliveryOption option) {
        if (cartTotal >= FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        if (option == null) {
            throw new IllegalArgumentException("L'option de livraison doit être spécifiée si le montant est inférieur au seuil de livraison gratuite.");
        }
        return option.getBaseCost();
    }

    public static boolean isFreeShipping(double cartTotal) {
        return cartTotal >= FREE_SHIPPING_THRESHOLD;
    }
}
