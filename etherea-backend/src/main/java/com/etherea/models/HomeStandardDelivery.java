package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

@Entity
public class HomeStandardDelivery extends DeliveryMethod {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final double STANDARD_SHIPPING_COST = 5.0;
    private static final int DELIVERY_DAYS = 7;
    public HomeStandardDelivery() {}
    public HomeStandardDelivery(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    @Override
    public double calculateCost(double orderAmount) {
        return orderAmount < FREE_SHIPPING_THRESHOLD ? STANDARD_SHIPPING_COST : 0.0;
    }
    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;  // Délai en jours ouvrés
    }
    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.HOME_STANDARD;
    }
    @Override
    public String getDescription() {
        return "Livraison à domicile standard";
    }
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
