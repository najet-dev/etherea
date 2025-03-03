package com.etherea.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddDeliveryMethodRequestDTO {
    @NotNull(message = "User ID cannot be null.")
    @Positive(message = "The user ID must be a positive number.")
    private Long userId;
    @NotNull(message = "The delivery type ID cannot be null.")
    @Positive(message = "The delivery type ID must be a positive number.")
    private Long deliveryTypeId;
    private Long addressId;
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    @NotNull(message = "The order amount cannot be null.")
    @Min(value = 0, message = "The order amount cannot be negative.")
    private BigDecimal orderAmount;
    public AddDeliveryMethodRequestDTO() {}
    public AddDeliveryMethodRequestDTO(Long userId, Long deliveryTypeId, Long addressId,
                                       String pickupPointName, String pickupPointAddress,
                                       Double pickupPointLatitude, Double pickupPointLongitude,
                                       BigDecimal orderAmount) {
        this.userId = userId;
        this.deliveryTypeId = deliveryTypeId;
        this.addressId = addressId;
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
        this.orderAmount = orderAmount;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
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
    public BigDecimal getOrderAmount() {
        return orderAmount;
    }
    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

}
