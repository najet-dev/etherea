package com.etherea.dtos;

import com.etherea.models.PickupPointDetails;

import java.math.BigDecimal;

public class PickupPointDetailsDTO {
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    public PickupPointDetailsDTO() {}
    public PickupPointDetailsDTO(String pickupPointName, String pickupPointAddress, Double pickupPointLatitude, Double pickupPointLongitude) {
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
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

    // Vérifie que la latitude et la longitude sont dans des plages valides
    private BigDecimal validateCoordinate(BigDecimal coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être nulles.");
        }
        return coordinate;
    }
}
