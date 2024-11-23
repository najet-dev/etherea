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

    /**
     * Vérifie si la livraison est gratuite.
     *
     * @param totalAmount Le montant total du panier.
     * @return true si la livraison est gratuite, sinon false.
     */
    public boolean isFreeShipping(double totalAmount) {
        return totalAmount >= FREE_SHIPPING_THRESHOLD;
    }
    public double calculateCost(double totalAmount) {
        return isFreeShipping(totalAmount) ? 0.0 : getDeliveryOption().getBaseCost();
    }
    public abstract int calculateDeliveryTime();
    public abstract DeliveryOption getDeliveryOption();
    public abstract String getDescription();
}
