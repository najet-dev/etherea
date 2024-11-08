package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class PickupPointDelivery extends DeliveryMethod {

    private static final double FREE_SHIPPING_THRESHOLD = 50.0; // Seuil pour livraison gratuite
    private static final double SHIPPING_COST = 3.0; // Coût de livraison standard
    private static final int DELIVERY_DAYS = 8; // Délai de livraison standard
    @Column(nullable = false)
    private String pickupPointName;
    @Column(nullable = false)
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;

    public PickupPointDelivery() {}

    public PickupPointDelivery(String pickupPointName, String pickupPointAddress,
                               Double pickupPointLatitude, Double pickupPointLongitude) {
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
    }

    @Override
    public double calculateCost(double orderAmount) {
        return orderAmount < FREE_SHIPPING_THRESHOLD ? SHIPPING_COST : 0.0;
    }

    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;  // Délai en jours ouvrés
    }

    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.PICKUP_POINT;
    }

    @Override
    public String getDescription() {
        return "Livraison au point relais";
    }

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
}
