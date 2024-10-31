package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import java.time.LocalDate;

public class PickupPointDeliveryDTO extends com.etherea.dto.DeliveryMethodDTO {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    public PickupPointDeliveryDTO() {}

    public PickupPointDeliveryDTO(Long id, DeliveryOption deliveryOption, LocalDate expectedDeliveryDate, Double cost,
                                  String name, String address, Double latitude, Double longitude) {
        super(id, deliveryOption, expectedDeliveryDate, cost);
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters pour les attributs spécifiques
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
