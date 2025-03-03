package com.etherea.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class PickupPointDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String pickupPointName;
    @NotBlank
    @Column(nullable = false)
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    public PickupPointDetails() {}
    public PickupPointDetails(String pickupPointName, String pickupPointAddress, Double pickupPointLatitude, Double pickupPointLongitude) {
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
    }
    public boolean isValid() {
        return pickupPointName != null && !pickupPointName.isBlank() &&
                pickupPointAddress != null && !pickupPointAddress.isBlank();
    }
    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPickupPointName() { return pickupPointName; }
    public void setPickupPointName(String pickupPointName) { this.pickupPointName = pickupPointName; }
    public String getPickupPointAddress() { return pickupPointAddress; }
    public void setPickupPointAddress(String pickupPointAddress) { this.pickupPointAddress = pickupPointAddress; }
    public Double getPickupPointLatitude() { return pickupPointLatitude; }
    public void setPickupPointLatitude(Double pickupPointLatitude) { this.pickupPointLatitude = pickupPointLatitude; }
    public Double getPickupPointLongitude() { return pickupPointLongitude; }
    public void setPickupPointLongitude(Double pickupPointLongitude) { this.pickupPointLongitude = pickupPointLongitude; }
}
