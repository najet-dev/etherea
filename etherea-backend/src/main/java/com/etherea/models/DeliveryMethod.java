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
    @Enumerated(EnumType.STRING)
    private DeliveryOption deliveryOption;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    protected DeliveryMethod() {}
    public DeliveryMethod(User user, DeliveryOption deliveryOption) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (deliveryOption == null) {
            throw new IllegalArgumentException("Delivery option cannot be null.");
        }
        this.user = user;
        this.deliveryOption = deliveryOption;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }
    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public boolean isFreeShipping(double totalAmount) {
        return totalAmount >= FREE_SHIPPING_THRESHOLD;
    }
    public double calculateCost(double totalAmount) {
        return isFreeShipping(totalAmount) ? 0.0 : getDeliveryOption().getBaseCost();
    }
    public abstract int calculateDeliveryTime();
    public abstract String getDescription();
    public abstract LocalDate calculateExpectedDeliveryDate();
}
