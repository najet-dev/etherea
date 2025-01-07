package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class PickupPointDelivery extends DeliveryMethod {
    private static final int DELIVERY_DAYS = 8;
    private static final double DELIVERY_COST = 3.0;
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    public PickupPointDelivery() {}
    public PickupPointDelivery(String pickupPointName, String pickupPointAddress,
                               Double pickupPointLatitude, Double pickupPointLongitude, User user) {
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude != null ? pickupPointLatitude : 0.0;
        this.pickupPointLongitude = pickupPointLongitude != null ? pickupPointLongitude : 0.0;
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
        return "Pickup point delivery (8 working days)";
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
    @Override
    public double calculateCost(double totalAmount) {
        if (isFreeShipping(totalAmount)) {
            return 0.0;
        }
        return DELIVERY_COST;
    }
    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.plusDays(DELIVERY_DAYS);
    }
    @Override
    public String getFullAddress() {
        return String.format("%s, %s [Lat: %f, Lon: %f]",
                pickupPointName, pickupPointAddress, pickupPointLatitude, pickupPointLongitude);
    }
}
