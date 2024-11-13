package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

@Entity
public class PickupPointDelivery extends DeliveryMethod {
    private static final double SHIPPING_COST = 3.0; // Coût de livraison pour un point relais
    private static final int DELIVERY_DAYS = 8; // Délai de livraison en jours ouvrés
    @Column(nullable = false)
    private String pickupPointName;
    @Column(nullable = false)
    private String pickupPointAddress;
    @Column(nullable = false)
    private Double pickupPointLatitude = 0.0;
    @Column(nullable = false)
    private Double pickupPointLongitude = 0.0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    public PickupPointDelivery() {}
    public PickupPointDelivery(String pickupPointName, String pickupPointAddress,
                               Double pickupPointLatitude, Double pickupPointLongitude, User user) {
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude != null ? pickupPointLatitude : 0.0;
        this.pickupPointLongitude = pickupPointLongitude != null ? pickupPointLongitude : 0.0;
        this.user = user;
    }
    @Override
    public double calculateCost(double orderAmount) {
        // Utilise la méthode isFreeShipping pour déterminer si la livraison est gratuite
        return isFreeShipping(orderAmount) ? 0.0 : SHIPPING_COST;
    }
    @Override
    public int calculateDeliveryTime() {
        // Retourne le nombre de jours ouvrés pour la livraison en point relais
        return DELIVERY_DAYS;
    }
    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.PICKUP_POINT;
    }
    @Override
    public String getDescription() {
        // Retourne la description de cette méthode de livraison
        return "Livraison au point relais";
    }

    // Getters et Setters
    public String getPickupPointName() {
        return pickupPointName;
    }
    public void setPickupPointName(String pickupPointName) {
        this.pickupPointName = pickupPointName;
    }
    public String getPickupPointAddress() {
        return pickupPointAddress;
    }
    public void setPickupPointAddress(String pickupPointAddress) {
        this.pickupPointAddress = pickupPointAddress;
    }
    public Double getPickupPointLatitude() {
        return pickupPointLatitude;
    }
    public void setPickupPointLatitude(Double pickupPointLatitude) {
        this.pickupPointLatitude = pickupPointLatitude;
    }

    public Double getPickupPointLongitude() {
        return pickupPointLongitude;
    }
    public void setPickupPointLongitude(Double pickupPointLongitude) {
        this.pickupPointLongitude = pickupPointLongitude;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
