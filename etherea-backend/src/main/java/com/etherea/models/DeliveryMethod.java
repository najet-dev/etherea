// Classe principale
package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DeliveryOption deliveryOption;
    private LocalDate expectedDeliveryDate;
    private Double cost;

    public DeliveryMethod() {}

    public DeliveryMethod(DeliveryOption deliveryOption, LocalDate expectedDeliveryDate, Double cost) {
        this.deliveryOption = deliveryOption;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.cost = cost;
    }
    public abstract LocalDate calculateExpectedDeliveryDate();
    public abstract Double calculateCost(Double orderAmount);

    // Getters et Setters
    public Long getId() { return id; }
    public DeliveryOption getDeliveryOption() { return deliveryOption; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) {
        if (cost != null && cost < 0) {
            throw new IllegalArgumentException("Cost must be non-negative.");
        }
        this.cost = cost;
    }
}
