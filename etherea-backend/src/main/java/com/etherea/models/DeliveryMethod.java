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
    protected static final double FREE_SHIPPING_THRESHOLD = 50.0;
    public Long getId() {
        return id;
    }

    // Method to check if delivery is free
    public boolean isFreeShipping(double totalAmount) {
        return totalAmount >= FREE_SHIPPING_THRESHOLD;
    }

    // Abstract methods
    public abstract double calculateCost(double totalAmount);
    public abstract int calculateDeliveryTime();
    public abstract DeliveryOption getDeliveryOption();
    public abstract String getDescription();
}
