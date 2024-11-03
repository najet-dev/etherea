package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "delivery_type")
public abstract class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryOption deliveryOption;

    private LocalDate expectedDeliveryDate;
    private Double cost;

    // Constructeur par défaut requis par JPA
    public DeliveryMethod() {}

    public DeliveryMethod(DeliveryOption deliveryOption, Double orderAmount) {
        this.deliveryOption = deliveryOption;
        this.expectedDeliveryDate = calculateExpectedDeliveryDate();
        this.cost = calculateCost(orderAmount);
    }

    // Méthodes abstraites pour le calcul de la date de livraison et du coût
    public abstract LocalDate calculateExpectedDeliveryDate();
    public abstract Double calculateCost(Double orderAmount);

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DeliveryOption getDeliveryOption() { return deliveryOption; }
    public void setDeliveryOption(DeliveryOption deliveryOption) { this.deliveryOption = deliveryOption; }

    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        if (expectedDeliveryDate == null || expectedDeliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expected delivery date must be in the future.");
        }
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Double getCost() { return cost; }
    public void setCost(Double cost) {
        if (cost == null || cost < 0) {
            throw new IllegalArgumentException("Cost must be non-negative.");
        }
        this.cost = cost;
    }
}
