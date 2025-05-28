package com.etherea.utils;

import java.math.BigDecimal;

public class FreeShippingChecker {
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(50.0);
    private FreeShippingChecker() {}

    /**
     * Determines whether the given cart total qualifies for free shipping.
     *
     * @param cartTotal the total amount in the shopping cart
     * @return true if the cart total is greater than or equal to the free shipping threshold, false otherwise
     * @throws IllegalArgumentException if the cart total is null
     */
    public static boolean isFreeShipping(BigDecimal cartTotal) {
        if (cartTotal == null) {
            throw new IllegalArgumentException("The cart amount cannot be null.");
        }
        return cartTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0;
    }
}
