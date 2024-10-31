package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class HomeExpressDelivery extends DeliveryMethod {
    private static final Double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final Double EXPRESS_SHIPPING_COST = 8.0;
    public HomeExpressDelivery() {
        super(DeliveryOption.HOME_EXPRESS, LocalDate.now().plusDays(2), EXPRESS_SHIPPING_COST);
    }
    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(2);
    }
    @Override
    public Double calculateCost(Double orderAmount) {
        return (orderAmount < FREE_SHIPPING_THRESHOLD) ? EXPRESS_SHIPPING_COST : 0.0;
    }
}
