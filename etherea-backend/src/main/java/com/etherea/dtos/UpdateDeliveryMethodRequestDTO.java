package com.etherea.dtos;

public class UpdateDeliveryMethodRequestDTO {
    private Long userId;
    private Long deliveryMethodId;
    private Long deliveryTypeId;
    private Long addressId;
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    public UpdateDeliveryMethodRequestDTO() {}
    public UpdateDeliveryMethodRequestDTO(Long userId, Long deliveryMethodId, Long deliveryTypeId,
                                          Long addressId, String pickupPointName, String pickupPointAddress,
                                          Double pickupPointLatitude, Double pickupPointLongitude) {
        this.userId = userId;
        this.deliveryMethodId = deliveryMethodId;
        this.deliveryTypeId = deliveryTypeId;
        this.addressId = addressId;
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getDeliveryMethodId() {
        return deliveryMethodId;
    }
    public void setDeliveryMethodId(Long deliveryMethodId) {
        this.deliveryMethodId = deliveryMethodId;
    }
    public Long getDeliveryTypeId() {
        return deliveryTypeId;
    }
    public void setDeliveryTypeId(Long deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
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
}