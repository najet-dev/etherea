package com.etherea.dtos;

import com.etherea.models.PickupPoint;

public class PickupPointDTO {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    public PickupPointDTO() {}
    public PickupPointDTO(Long id, String name, String address, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
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
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // Conversion de l'entité vers le DTO
    public static PickupPointDTO fromPickupPoint(PickupPoint pickupPoint) {
        return new PickupPointDTO(
                pickupPoint.getId(),
                pickupPoint.getName(),
                pickupPoint.getAddress(),
                pickupPoint.getLatitude(),
                pickupPoint.getLongitude()
        );
    }

    // Conversion du DTO vers l'entité
    public PickupPoint toPickupPoint() {
        PickupPoint pickupPoint = new PickupPoint();
        pickupPoint.setId(this.id);
        pickupPoint.setName(this.name);
        pickupPoint.setAddress(this.address);
        pickupPoint.setLatitude(this.latitude);
        pickupPoint.setLongitude(this.longitude);
        return pickupPoint;
    }
}
