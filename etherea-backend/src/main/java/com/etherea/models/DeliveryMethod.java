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
    // Méthode abstraite pour calculer le coût de livraison
    public abstract double calculateCost(double totalAmount);

    // Méthode abstraite pour calculer le temps de livraison
    public abstract int calculateDeliveryTime(); // en jours ouvrés

    // Méthode abstraite pour récupérer l'option de livraison
    public abstract DeliveryOption getDeliveryOption();

    // Méthode abstraite pour obtenir la description de la livraison
    public abstract String getDescription();
    public boolean isFreeShipping(double totalAmount) {
        return totalAmount >= FREE_SHIPPING_THRESHOLD;
    }
}
