package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class HomeStandardDelivery extends DeliveryMethod {
    private static final int DELIVERY_DAYS = 7;
    private static final double DELIVERY_COST = 5.0;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private DeliveryAddress deliveryAddress;
    public HomeStandardDelivery() {
        super();
    }
    public HomeStandardDelivery(DeliveryAddress deliveryAddress, User user) {
        super(user, DeliveryOption.HOME_STANDARD);
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
        return "Standard home delivery (7 working days)";
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
