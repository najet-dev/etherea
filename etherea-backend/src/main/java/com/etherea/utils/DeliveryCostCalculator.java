package com.etherea.utils;

import com.etherea.enums.DeliveryOption;

public class DeliveryCostCalculator {

    /**
     * Calcule les frais de livraison en fonction du total du panier et du type de livraison.
     *
     * @param cartTotal Le montant total du panier.
     * @param option    L'option de livraison.
     * @return Les frais de livraison.
     */
    public static double calculateDeliveryCost(double cartTotal, DeliveryOption option) {
        switch (option) {
            case HOME_STANDARD:
                return cartTotal < 50 ? 5.0 : 0.0;
            case HOME_EXPRESS:
                return cartTotal < 50 ? 10.0 : 5.0;
            case PICKUP_POINT:
                return cartTotal < 50 ? 3.0 : 0.0;
            default:
                throw new IllegalArgumentException("Option de livraison invalide.");
        }
    }
}
