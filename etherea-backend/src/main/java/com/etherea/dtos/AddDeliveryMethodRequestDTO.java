package com.etherea.dtos;

import com.etherea.enums.DeliveryType;

import java.math.BigDecimal;

public class AddDeliveryMethodRequestDTO {
    private Long userId;
    private DeliveryType deliveryType;
    private Long addressId;
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    private double orderAmount;
    public AddDeliveryMethodRequestDTO() {}
    public AddDeliveryMethodRequestDTO(Long userId, DeliveryType deliveryType, Long addressId,
                                       String pickupPointName, String pickupPointAddress,
                                       Double pickupPointLatitude, Double pickupPointLongitude,
                                       double orderAmount) {
        this.setUserId(userId);
        this.setDeliveryType(deliveryType);
        this.setAddressId(addressId);
        this.setPickupPointName(pickupPointName);
        this.setPickupPointAddress(pickupPointAddress);
        this.setPickupPointLatitude(pickupPointLatitude);
        this.setPickupPointLongitude(pickupPointLongitude);
        this.setOrderAmount(orderAmount);
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        this.userId = userId;
    }
    public DeliveryType getDeliveryType() {
        return deliveryType;
    }
    public void setDeliveryType(DeliveryType deliveryType) {
        if (deliveryType == null) {
            throw new IllegalArgumentException("Delivery type must not be null.");
        }
        this.deliveryType = deliveryType;
    }
    public Long getAddressId() {
        return addressId;
    }
    public void setAddressId(Long addressId) {
        if (isHomeDelivery() && (addressId == null || addressId <= 0)) {
            throw new IllegalArgumentException("Address ID is required for home delivery.");
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
        if (isPickupPoint() && pickupPointLatitude == null) {
            throw new IllegalArgumentException("Latitude is required for pickup point.");
        }
        this.pickupPointLatitude = pickupPointLatitude;
    }
    public Double getPickupPointLongitude() {
        return pickupPointLongitude;
    }
    public void setPickupPointLongitude(Double pickupPointLongitude) {
        if (isPickupPoint() && pickupPointLongitude == null) {
            throw new IllegalArgumentException("Longitude is required for pickup point.");
        }
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
    // Méthodes utilitaires pour simplifier la gestion des types de livraison
    public boolean isPickupPoint() {
        return this.deliveryType == DeliveryType.PICKUP_POINT;
    }

    public boolean isHomeDelivery() {
        return this.deliveryType == DeliveryType.HOME_STANDARD || this.deliveryType == DeliveryType.HOME_EXPRESS;
    }
}
