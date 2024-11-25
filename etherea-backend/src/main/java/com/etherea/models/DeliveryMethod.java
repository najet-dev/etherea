package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

@Entity
@Table(name = "delivery_method")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    public Long getId() {
        return id;
    }
    public boolean isFreeShipping(double totalAmount) {
        return totalAmount >= FREE_SHIPPING_THRESHOLD;
    }
    public double calculateCost(double totalAmount) {
        if (isFreeShipping(totalAmount)) {
            return 0.0;
        }
        return getDeliveryOption().getBaseCost();
    }
    public abstract int calculateDeliveryTime();
    public abstract DeliveryOption getDeliveryOption();
    public abstract String getDescription();
}
