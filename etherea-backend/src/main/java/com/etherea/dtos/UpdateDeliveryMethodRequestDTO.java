package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;

public class UpdateDeliveryMethodRequestDTO {
    private Long deliveryMethodId;
    private DeliveryOption deliveryOption;
    private Long userId;
    private Long addressId;
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;

    public UpdateDeliveryMethodRequestDTO() {}

    // Getters et Setters
    public Long getDeliveryMethodId() {
        return deliveryMethodId;
    }
    public void setDeliveryMethodId(Long deliveryMethodId) {
        if (deliveryMethodId == null || deliveryMethodId <= 0) {
            throw new IllegalArgumentException("Delivery Method ID must be a positive number.");
        }
        this.deliveryMethodId = deliveryMethodId;
    }
    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }
    public void setDeliveryOption(DeliveryOption deliveryOption) {
        if (deliveryOption == null) {
            throw new IllegalArgumentException("Delivery option must not be null.");
        }
        this.deliveryOption = deliveryOption;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getAddressId() {
        return addressId;
    }
    public void setAddressId(Long addressId) {
        if (addressId == null || addressId <= 0) {
            throw new IllegalArgumentException("Delivery address ID must be a positive number.");
        }
        this.addressId = addressId;
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
}
