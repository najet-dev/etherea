package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class PickupPointDelivery extends DeliveryMethod {

    private static final Double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final Double SHIPPING_COST = 3.0;

    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    public PickupPointDelivery() {
        this(null, null, null, null);
    }

    public PickupPointDelivery(String name, String address, Double latitude, Double longitude) {
        super(DeliveryOption.PICKUP_POINT, LocalDate.now().plusDays(8), SHIPPING_COST);
        this.name = name;
        this.address = address;
        setLatitude(latitude);
        setLongitude(longitude);
    }

    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(8);
    }

    @Override
    public Double calculateCost(Double orderAmount) {
        return (orderAmount < FREE_SHIPPING_THRESHOLD) ? SHIPPING_COST : 0.0;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }
        this.latitude = latitude;
    }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) {
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }
        this.longitude = longitude;
    }
}
