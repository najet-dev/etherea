package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class HomeStandardDelivery extends DeliveryMethod {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    private static final int DELIVERY_DAYS = 7;
    private static final double DELIVERY_COST = 5.0;
    public HomeStandardDelivery() {}
    public HomeStandardDelivery(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;
    }
    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.HOME_STANDARD;
    }
    @Override
    public String getDescription() {
        return "Standard home delivery (7 working days)";
    }
    @Override
    public double calculateCost(double totalAmount) {
        if (isFreeShipping(totalAmount)) {
            return 0.0;
        }
        return DELIVERY_COST;
    }
    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.plusDays(DELIVERY_DAYS);
    }
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
