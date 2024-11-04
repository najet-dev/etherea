package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

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

    public DeliveryMethod() {}

    public DeliveryMethod(DeliveryOption deliveryOption, Double orderAmount) {
        this.deliveryOption = Objects.requireNonNull(deliveryOption, "DeliveryOption ne peut pas être nul.");
        this.expectedDeliveryDate = calculateExpectedDeliveryDate();
        this.cost = calculateCost(orderAmount);
    }

    // Méthodes abstraites pour calculer la date de livraison et le coût
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
            throw new IllegalArgumentException("La date de livraison prévue doit être dans le futur.");
        }
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Double getCost() { return cost; }
    public void setCost(Double cost) {
        if (cost == null || cost < 0) {
            throw new IllegalArgumentException("Le coût doit être non négatif.");
        }
        this.cost = cost;
    }
}
