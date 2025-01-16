package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class HomeStandardDelivery extends DeliveryMethod {
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "delivery_address_id", referencedColumnName = "id")
    private DeliveryAddress deliveryAddress;
    private static final int DELIVERY_DAYS = 7;
    private static final double DELIVERY_COST = 5.0;
    public HomeStandardDelivery() {
        super();
    }
    public HomeStandardDelivery(DeliveryAddress deliveryAddress, User user) {
        super();
        this.setDeliveryAddress(deliveryAddress);
        this.setUser(user);
        this.setDeliveryOption(DeliveryOption.HOME_STANDARD);
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
}