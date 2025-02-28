package com.etherea.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddDeliveryMethodRequestDTO {

    @NotNull(message = "L'ID de l'utilisateur ne peut pas être nul.")
    @Positive(message = "L'ID de l'utilisateur doit être un nombre positif.")
    private Long userId;
    @NotNull(message = "L'ID du type de livraison ne peut pas être null.")
    @Positive(message = "L'ID du type de livraison doit être un nombre positif.")
    private DeliveryTypeDTO deliveryType;
    private Long addressId;
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    @NotNull(message = "Le montant de la commande ne peut pas être null.")
    @Min(value = 0, message = "Le montant de la commande ne peut pas être négatif.")
    private BigDecimal orderAmount;
    public AddDeliveryMethodRequestDTO() {}
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public DeliveryTypeDTO getDeliveryType() {
        return deliveryType;
    }
    public void setDeliveryType(DeliveryTypeDTO deliveryType) {
        this.deliveryType = deliveryType;
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
    public AddDeliveryMethodRequestDTO(Long userId, DeliveryTypeDTO deliveryType, Long addressId,
                                       String pickupPointName, String pickupPointAddress,
                                       Double pickupPointLatitude, Double pickupPointLongitude,
                                       BigDecimal orderAmount) {
        this.userId = userId;
        this.deliveryType = deliveryType;
        this.addressId = addressId;
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
        this.orderAmount = orderAmount;
    }
}
