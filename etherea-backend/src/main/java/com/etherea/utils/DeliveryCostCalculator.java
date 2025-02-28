package com.etherea.utils;

import com.etherea.models.DeliveryType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DeliveryCostCalculator {
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(50.0);
    public static BigDecimal calculateDeliveryCost(BigDecimal cartTotal, DeliveryType deliveryType) {
        if (deliveryType == null) {
            throw new IllegalArgumentException("The delivery type cannot be null.");
        }
        if (cartTotal == null) {
            throw new IllegalArgumentException("the shopping cart amount cannot be null.");
        }

        return cartTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : deliveryType.getCost().setScale(2, RoundingMode.HALF_UP);
    }
}
