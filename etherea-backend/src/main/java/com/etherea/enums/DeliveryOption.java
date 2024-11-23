package com.etherea.enums;

public enum DeliveryOption {
    HOME_STANDARD(5.0),
    HOME_EXPRESS(10.0),
    PICKUP_POINT(3.0);
    private final double baseCost;

    // Constructor
    DeliveryOption(double baseCost) {
        this.baseCost = baseCost;
    }
    public double getBaseCost() {
        return this.baseCost;
    }
}
