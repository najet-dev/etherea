package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("HOME_EXPRESS")
public class HomeExpressDelivery extends DeliveryMethod {
    private static final Double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final Double EXPRESS_SHIPPING_COST = 8.0;
    private static final int DELIVERY_DAYS = 2;

    public HomeExpressDelivery(Double orderAmount) {
        super(DeliveryOption.HOME_EXPRESS, orderAmount);
    }
    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(DELIVERY_DAYS);
    }
    @Override
    public Double calculateCost(Double orderAmount) {
        if (orderAmount == null || orderAmount < 0) {
            throw new IllegalArgumentException("Le montant de la commande doit être non négatif.");
        }
        return (orderAmount < FREE_SHIPPING_THRESHOLD) ? EXPRESS_SHIPPING_COST : 0.0;
    }
}
