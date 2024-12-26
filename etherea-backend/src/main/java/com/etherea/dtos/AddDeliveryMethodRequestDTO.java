package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;

public class AddDeliveryMethodRequestDTO {
    private Long userId;
    private DeliveryOption deliveryOption;
    private Long addressId;
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    private double orderAmount;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        this.userId = userId;
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
    public Long getAddressId() {
        return addressId;
    }
    public void setAddressId(Long addressId) {
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
    public double getOrderAmount() {
        return orderAmount;
    }
    public void setOrderAmount(double orderAmount) {
        if (orderAmount < 0) {
            throw new IllegalArgumentException("Order amount cannot be negative.");
        }
        this.orderAmount = orderAmount;
    }
}
