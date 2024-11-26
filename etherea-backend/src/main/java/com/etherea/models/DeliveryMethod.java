package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "delivery_method")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double deliveryCost;
    private LocalDate expectedDeliveryDate;
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    public Long getId() {
        return id;
    }
    public double getDeliveryCost() {
        return deliveryCost;
    }
    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
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

    public abstract LocalDate calculateExpectedDeliveryDate();
}
