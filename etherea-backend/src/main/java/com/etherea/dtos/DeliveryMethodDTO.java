package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.models.HomeExpressDelivery;
import com.etherea.models.HomeStandardDelivery;
import com.etherea.models.PickupPointDelivery;
import java.time.LocalDate;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryOption deliveryOption;
    private LocalDate expectedDeliveryDate;
    private Double cost;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    public DeliveryMethodDTO() {}

    public DeliveryMethodDTO(Long id, DeliveryOption deliveryOption, LocalDate expectedDeliveryDate, Double cost,
                             String name, String address, Double latitude, Double longitude) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.expectedDeliveryDate = expectedDeliveryDate;
        setCost(cost);
        this.name = name;
        this.address = address;
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public Double getCost() { return cost; }
    public void setCost(Double cost) {
        if (cost != null && cost < 0) {
            throw new IllegalArgumentException("Cost must be non-negative.");
        }
        this.cost = cost;
    }

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

    public DeliveryMethod toDeliveryMethod(Double orderAmount) {
        switch (this.deliveryOption) {
            case HOME_EXPRESS:
                HomeExpressDelivery expressDelivery = new HomeExpressDelivery(orderAmount);
                expressDelivery.setId(this.id);
                expressDelivery.setExpectedDeliveryDate(this.expectedDeliveryDate);
                expressDelivery.setCost(this.cost);
                return expressDelivery;
            case HOME_STANDARD:
                HomeStandardDelivery standardDelivery = new HomeStandardDelivery(orderAmount);
                standardDelivery.setId(this.id);
                standardDelivery.setExpectedDeliveryDate(this.expectedDeliveryDate);
                standardDelivery.setCost(this.cost);
                return standardDelivery;
            case PICKUP_POINT:
                if (name == null || name.isEmpty() || address == null || address.isEmpty() || latitude == null || longitude == null) {
                    throw new IllegalArgumentException("Name, address, latitude, and longitude are required for pickup point delivery.");
                }
                PickupPointDelivery pickupPointDelivery = new PickupPointDelivery(name, address, latitude, longitude, orderAmount);
                pickupPointDelivery.setId(this.id);
                pickupPointDelivery.setExpectedDeliveryDate(this.expectedDeliveryDate);
                pickupPointDelivery.setCost(this.cost);
                return pickupPointDelivery;
            default:
                throw new IllegalArgumentException("Unsupported DeliveryOption type.");
        }
    }
}
