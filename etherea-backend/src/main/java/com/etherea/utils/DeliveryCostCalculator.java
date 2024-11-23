package com.etherea.utils;

import com.etherea.enums.DeliveryOption;

public class DeliveryCostCalculator {
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    public static double calculateDeliveryCost(double cartTotal, DeliveryOption option) {
        return cartTotal >= FREE_SHIPPING_THRESHOLD ? 0.0 : option.getBaseCost();
    }
}
