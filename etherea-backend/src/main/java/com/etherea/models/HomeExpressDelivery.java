package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

@Entity
public class HomeExpressDelivery extends DeliveryMethod {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    private static final int DELIVERY_DAYS = 2;
    public HomeExpressDelivery() {}
    public HomeExpressDelivery(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public double calculateCost(double totalAmount) {
        // Si la livraison est gratuite (montant du panier >= 50), le coût est 0, sinon c'est le coût de base
        return isFreeShipping(totalAmount) ? 0.0 : DeliveryOption.HOME_EXPRESS.getBaseCost();
    }

    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;
    }
    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.HOME_EXPRESS;
    }
    @Override
    public String getDescription() {
        return "Express home delivery (2 working days)";
    }
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
