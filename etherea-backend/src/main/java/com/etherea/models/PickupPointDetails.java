package com.etherea.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class PickupPointDetails {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    public PickupPointDetails() {}
    public PickupPointDetails(String name, String address, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
