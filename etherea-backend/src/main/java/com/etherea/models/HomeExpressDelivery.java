package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class HomeExpressDelivery extends DeliveryMethod {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    private static final int DELIVERY_DAYS = 2;  // 2 jours de livraison pour express
    private static final double DELIVERY_COST = 10.0;  // Coût de la livraison express

    public HomeExpressDelivery() {}

    public HomeExpressDelivery(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;  // Temps de livraison pour express
    }

    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.HOME_EXPRESS;  // Option de livraison express
    }

    @Override
    public String getDescription() {
        return "Express home delivery (2 working days)";  // Description de la livraison express
    }

    @Override
    public double calculateCost(double totalAmount) {
        if (isFreeShipping(totalAmount)) {
            return 0.0;  // Livraison gratuite si le montant total est supérieur ou égal au seuil
        }
        return DELIVERY_COST;  // Coût fixe de 10 € pour la livraison express
    }

    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        LocalDate currentDate = LocalDate.now();  // Date actuelle
        return currentDate.plusDays(DELIVERY_DAYS);  // Ajouter 2 jours pour la livraison express
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
