package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeliveryOption deliveryOption;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_method_id")
    private List<PickupPoint> pickupPoints;

    private LocalDate expectedDeliveryDate;
    private Double cost;
    private Double minimumAmountForFreeDelivery;

    // Constructeur sans argument pour JPA
    public DeliveryMethod() {}

    // Constructeur principal
    public DeliveryMethod(DeliveryOption deliveryOption, List<PickupPoint> pickupPoints,
                          LocalDate expectedDeliveryDate, Double cost,
                          Double minimumAmountForFreeDelivery) {
        this.deliveryOption = deliveryOption;
        this.pickupPoints = pickupPoints;
        this.expectedDeliveryDate = expectedDeliveryDate;
        setCost(cost);
        setMinimumAmountForFreeDelivery(minimumAmountForFreeDelivery);
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public List<PickupPoint> getPickupPoints() {
        return pickupPoints;
    }

    public void setPickupPoints(List<PickupPoint> pickupPoints) {
        this.pickupPoints = pickupPoints;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        if (cost != null && cost < 0) {
            throw new IllegalArgumentException("Cost must be non-negative.");
        }
        this.cost = cost;
    }

    public Double getMinimumAmountForFreeDelivery() {
        return minimumAmountForFreeDelivery;
    }

    public void setMinimumAmountForFreeDelivery(Double minimumAmountForFreeDelivery) {
        if (minimumAmountForFreeDelivery != null && minimumAmountForFreeDelivery < 0) {
            throw new IllegalArgumentException("Minimum amount for free delivery must be non-negative.");
        }
        this.minimumAmountForFreeDelivery = minimumAmountForFreeDelivery;
    }
}
