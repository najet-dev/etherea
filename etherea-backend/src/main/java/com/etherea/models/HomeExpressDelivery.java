package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class HomeExpressDelivery extends DeliveryMethod {
    private static final int DELIVERY_DAYS = 2;
    private static final double DELIVERY_COST = 10.0;
    public HomeExpressDelivery() {
        super();
    }
    public HomeExpressDelivery(DeliveryAddress deliveryAddress, User user) {
        super();
        this.setDeliveryAddress(deliveryAddress);
        this.setUser(user);
        this.setDeliveryOption(DeliveryOption.HOME_EXPRESS);
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