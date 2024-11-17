package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

@Entity
public class PickupPointDelivery extends DeliveryMethod {
    private static final double SHIPPING_COST = 3.0;
    private static final int DELIVERY_DAYS = 8;
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
    public double calculateCost(double totalAmount) {
        return isFreeShipping(totalAmount) ? 0.0 : SHIPPING_COST;
    }
    @Override
    public int calculateDeliveryTime() {
        return DELIVERY_DAYS;
    }
    @Override
    public DeliveryOption getDeliveryOption() {
        return DeliveryOption.PICKUP_POINT;
    }
    @Override
    public String getDescription() {
        return "Livraison au point relais (8 jours ouvrés)";
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
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}