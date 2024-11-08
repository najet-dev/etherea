package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

@Entity
public class HomeExpressDelivery extends DeliveryMethod {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final double EXPRESS_SHIPPING_COST = 8.0;
    private static final int DELIVERY_DAYS = 2;

    public HomeExpressDelivery() {}
    public HomeExpressDelivery(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    @Override
    public double calculateCost(double orderAmount) {
        return orderAmount < FREE_SHIPPING_THRESHOLD ? EXPRESS_SHIPPING_COST : 0.0;
    }
    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;  // Délai en jours ouvrés
    }
    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.HOME_EXPRESS;
    }
    @Override
    public String getDescription() {
        return "Livraison à domicile express";
    }
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
