package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class HomeStandardDelivery extends DeliveryMethod {
    private static final Double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final Double STANDARD_SHIPPING_COST = 5.0;
    public HomeStandardDelivery() {
        super(DeliveryOption.HOME_STANDARD, LocalDate.now().plusDays(5), STANDARD_SHIPPING_COST);
    }
    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(5);
    }
    @Override
    public Double calculateCost(Double orderAmount) {
        return (orderAmount < FREE_SHIPPING_THRESHOLD) ? STANDARD_SHIPPING_COST : 0.0;
    }
}
