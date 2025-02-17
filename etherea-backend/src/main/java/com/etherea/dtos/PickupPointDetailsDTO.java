package com.etherea.dtos;

import java.math.BigDecimal;

public class PickupPointDetailsDTO {
    private Long id;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public PickupPointDetailsDTO() {}

    public PickupPointDetailsDTO(Long id, String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = validateCoordinate(latitude);
        this.longitude = validateCoordinate(longitude);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public BigDecimal getLatitude() {
        return latitude;
    }
    public void setLatitude(BigDecimal latitude) {
        this.latitude = validateCoordinate(latitude);
    }
    public BigDecimal getLongitude() {
        return longitude;
    }
    public void setLongitude(BigDecimal longitude) {
        this.longitude = validateCoordinate(longitude);
    }
    // Vérifie que la latitude et la longitude sont dans des plages valides
    private BigDecimal validateCoordinate(BigDecimal coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être nulles.");
        }
        return coordinate;
    }
}
