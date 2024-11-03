package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("HOME_STANDARD")
public class HomeStandardDelivery extends DeliveryMethod {
    private static final Double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final Double STANDARD_SHIPPING_COST = 5.0;
    private static final int DELIVERY_DAYS = 7;

    // Constructeur initialisant le coût basé sur le montant de la commande
    public HomeStandardDelivery(Double orderAmount) {
        super(DeliveryOption.HOME_STANDARD, orderAmount);
    }

    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(DELIVERY_DAYS);
    }

    @Override
    public Double calculateCost(Double orderAmount) {
        if (orderAmount == null || orderAmount < 0) {
            throw new IllegalArgumentException("Order amount must be non-negative.");
        }
        return (orderAmount < FREE_SHIPPING_THRESHOLD) ? STANDARD_SHIPPING_COST : 0.0;
    }
}
