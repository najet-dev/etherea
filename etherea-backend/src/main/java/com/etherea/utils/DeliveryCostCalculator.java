package com.etherea.utils;

import com.etherea.models.DeliveryMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DeliveryCostCalculator {

    private final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(50.0);

    public BigDecimal calculateDeliveryCost(BigDecimal cartTotal, DeliveryMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("La méthode de livraison ne peut pas être null.");
        }
        if (cartTotal == null) {
            throw new IllegalArgumentException("Le montant du panier ne peut pas être null.");
        }
        return cartTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : method.getCost();
    }
}
