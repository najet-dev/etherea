package com.etherea.enums;

public enum DeliveryOption {
    HOME_EXPRESS(10.0, 2, "Express home delivery (2 working days)"),
    HOME_STANDARD(5.0, 7, "Standard home delivery (7 working days)"),
    PICKUP_POINT(3.0, 8, "Pickup point delivery (8 working days)");

    private final double cost;
    private final int deliveryDays;
    private final String description;

    DeliveryOption(double cost, int deliveryDays, String description) {
        this.cost = cost;
        this.deliveryDays = deliveryDays;
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public int getDeliveryDays() {
        return deliveryDays;
    }

    public String getDescription() {
        return description;
    }
}
