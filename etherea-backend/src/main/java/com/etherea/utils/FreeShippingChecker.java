package com.etherea.utils;

import java.math.BigDecimal;

public class FreeShippingChecker {
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(50.0);
    private FreeShippingChecker() {}
    public static boolean isFreeShipping(BigDecimal cartTotal) {
        if (cartTotal == null) {
            throw new IllegalArgumentException("Le montant du panier ne peut pas être null.");
        }
        return cartTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0;
    }
}
