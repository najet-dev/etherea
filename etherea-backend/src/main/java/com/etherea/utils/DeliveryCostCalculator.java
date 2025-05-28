package com.etherea.utils;

import com.etherea.models.DeliveryType;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DeliveryCostCalculator {
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(50.0);

    /**
     * Calculates the delivery cost for a given cart total and delivery type.
     * If the cart total exceeds the free shipping threshold, the delivery cost is zero.
     *
     * @param cartTotal    the total amount in the shopping cart
     * @param deliveryType the type of delivery selected
     * @return the delivery cost, scaled to 2 decimal places
     * @throws IllegalArgumentException if either deliveryType or cartTotal is null
     */
    public BigDecimal calculateDeliveryCost(BigDecimal cartTotal, DeliveryType deliveryType) {
        if (deliveryType == null || cartTotal == null) {
            throw new IllegalArgumentException("Delivery type and cart total cannot be null.");
        }
        return isFreeShipping(cartTotal) ? BigDecimal.ZERO : deliveryType.getCost().setScale(2, RoundingMode.HALF_UP);
    }
    private boolean isFreeShipping(BigDecimal cartTotal) {
        return cartTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0;
    }
}
