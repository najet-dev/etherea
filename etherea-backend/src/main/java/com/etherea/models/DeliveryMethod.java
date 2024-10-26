package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class DeliveryMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DeliveryOption deliveryOption;
    @ManyToOne
    @JoinColumn(name = "pickup_point_id")
    private PickupPoint pickupPoint;
    private LocalDate expectedDeliveryDate;
    private Double cost;
    private Double minimumAmountForFreeDelivery;

    // Constructeur sans argument pour JPA
    public DeliveryMethod() {}

    // Constructeur principal avec paramètres
    public DeliveryMethod(Long id, DeliveryOption deliveryOption, PickupPoint pickupPoint, LocalDate expectedDeliveryDate, Double cost, Double minimumAmountForFreeDelivery) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.pickupPoint = pickupPoint;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.cost = cost;
        this.minimumAmountForFreeDelivery = minimumAmountForFreeDelivery;
        validateDeliveryMethod();
    }

    // Validation de la méthode de livraison
    private void validateDeliveryMethod() {
        if (deliveryOption == DeliveryOption.PICKUP_POINT && pickupPoint == null) {
            throw new IllegalArgumentException("PickupPoint must be provided for PICKUP_POINT delivery option.");
        }
        if ((deliveryOption == DeliveryOption.HOME_STANDARD || deliveryOption == DeliveryOption.HOME_EXPRESS) && pickupPoint != null) {
            throw new IllegalArgumentException("PickupPoint must be null for home delivery options.");
        }
        validateExpectedDeliveryDate();
    }

    // Validation de la date de livraison
    private void validateExpectedDeliveryDate() {
        if (expectedDeliveryDate != null && expectedDeliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expected delivery date cannot be in the past.");
        }
    }

    // Getters et setters avec validation
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
        validateDeliveryMethod();
    }
    public PickupPoint getPickupPoint() {
        return pickupPoint;
    }
    public void setPickupPoint(PickupPoint pickupPoint) {
        this.pickupPoint = pickupPoint;
        validateDeliveryMethod();
    }
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
        validateExpectedDeliveryDate();
    }
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
    }
    public Double getMinimumAmountForFreeDelivery() {
        return minimumAmountForFreeDelivery;
    }
    public void setMinimumAmountForFreeDelivery(Double minimumAmountForFreeDelivery) {
        this.minimumAmountForFreeDelivery = minimumAmountForFreeDelivery;
    }

}
