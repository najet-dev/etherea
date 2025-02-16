package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class HomeExpressDelivery extends DeliveryMethod {
    private static final int DELIVERY_DAYS = 2;
    private static final double DELIVERY_COST = 10.0;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    public HomeExpressDelivery() {
        super();
    }
    public HomeExpressDelivery(DeliveryAddress deliveryAddress, User user) {
        super(user, DeliveryOption.HOME_EXPRESS);
        this.deliveryAddress = deliveryAddress;
    }
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;
    }
    @Override
    public String getDescription() {
        return "Express home delivery (2 working days)";
    }
    @Override
    public double calculateCost(double totalAmount) {
        return isFreeShipping(totalAmount) ? 0.0 : DELIVERY_COST;
    }
    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(DELIVERY_DAYS);
    }
}
