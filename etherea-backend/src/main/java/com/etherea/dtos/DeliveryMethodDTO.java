package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.models.DeliveryAddress;
import com.etherea.models.HomeExpressDelivery;
import com.etherea.models.HomeStandardDelivery;
import com.etherea.models.PickupPointDelivery;
import com.etherea.utils.DeliveryDateCalculator;

import java.time.LocalDate;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryOption deliveryOption;
    private LocalDate expectedDeliveryDate;
    private Double cost;
    private DeliveryAddressDTO deliveryAddress; // pour les livraisons à domicile
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    public DeliveryMethodDTO() {}
    public DeliveryMethodDTO(Long id, DeliveryOption deliveryOption, LocalDate expectedDeliveryDate, Double cost,
                             DeliveryAddressDTO deliveryAddress, String pickupPointName, String pickupPointAddress,
                             Double pickupPointLatitude, Double pickupPointLongitude) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.cost = cost;
        this.deliveryAddress = deliveryAddress;
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }
    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
    }
    public DeliveryAddressDTO getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddressDTO deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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
    public static DeliveryMethodDTO fromDeliveryMethod(
            DeliveryMethod deliveryMethod,
            DeliveryAddress userAddress,
            LocalDate startDate,
            double orderAmount,
            DeliveryDateCalculator calculator
    ) {
        DeliveryAddressDTO addressDTO = userAddress != null ? DeliveryAddressDTO.fromDeliveryAddress(userAddress) : null;
        int deliveryDays = deliveryMethod.calculateDeliveryTime();
        LocalDate expectedDeliveryDate = calculator.calculateDeliveryDate(startDate, deliveryDays);
        Double cost = deliveryMethod.isFreeShipping(orderAmount) ? 0.0 : deliveryMethod.calculateCost(orderAmount);

        if (deliveryMethod instanceof PickupPointDelivery pickupPoint) {
            return new DeliveryMethodDTO(
                    pickupPoint.getId(),
                    DeliveryOption.PICKUP_POINT,
                    expectedDeliveryDate,
                    cost,
                    null,
                    pickupPoint.getPickupPointName(),
                    pickupPoint.getPickupPointAddress(),
                    pickupPoint.getPickupPointLatitude(),
                    pickupPoint.getPickupPointLongitude()
            );
        } else if (deliveryMethod instanceof HomeStandardDelivery standardDelivery) {
            return new DeliveryMethodDTO(
                    standardDelivery.getId(),
                    DeliveryOption.HOME_STANDARD,
                    expectedDeliveryDate,
                    cost,
                    addressDTO,
                    null, null, null, null
            );
        } else if (deliveryMethod instanceof HomeExpressDelivery expressDelivery) {
            return new DeliveryMethodDTO(
                    expressDelivery.getId(),
                    DeliveryOption.HOME_EXPRESS,
                    expectedDeliveryDate,
                    cost,
                    addressDTO,
                    null, null, null, null
            );
        } else {
            throw new IllegalArgumentException("Type de méthode de livraison non pris en charge");
        }
    }

}
