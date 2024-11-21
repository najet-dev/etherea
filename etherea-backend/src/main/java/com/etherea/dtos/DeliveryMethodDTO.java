package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.DeliveryMethodNotFoundException;
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
    private DeliveryAddressDTO deliveryAddress; // for home delivery
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;

    private DeliveryMethodDTO(Builder builder) {
        if (builder.deliveryOption == DeliveryOption.PICKUP_POINT && (builder.pickupPointName == null || builder.pickupPointAddress == null)) {
            throw new DeliveryMethodNotFoundException("Les informations du point de collecte sont manquantes pour une livraison en point relais.");

        }
        if ((builder.deliveryOption == DeliveryOption.HOME_STANDARD || builder.deliveryOption == DeliveryOption.HOME_EXPRESS) && builder.deliveryAddress == null) {
            throw new DeliveryAddressNotFoundException("L'adresse de livraison est obligatoire pour une livraison à domicile.");
        }
        this.id = builder.id;
        this.deliveryOption = builder.deliveryOption;
        this.expectedDeliveryDate = builder.expectedDeliveryDate;
        this.cost = builder.cost;
        this.deliveryAddress = builder.deliveryAddress;
        this.pickupPointName = builder.pickupPointName;
        this.pickupPointAddress = builder.pickupPointAddress;
        this.pickupPointLatitude = builder.pickupPointLatitude;
        this.pickupPointLongitude = builder.pickupPointLongitude;
    }

    // Getters and setters
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
    public static class Builder {
        private Long id;
        private DeliveryOption deliveryOption;
        private LocalDate expectedDeliveryDate;
        private Double cost;
        private DeliveryAddressDTO deliveryAddress; // for home delivery
        private String pickupPointName;
        private String pickupPointAddress;
        private Double pickupPointLatitude;
        private Double pickupPointLongitude;
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }
        public Builder setDeliveryOption(DeliveryOption deliveryOption) {
            this.deliveryOption = deliveryOption;
            return this;
        }
        public Builder setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
            this.expectedDeliveryDate = expectedDeliveryDate;
            return this;
        }
        public Builder setCost(Double cost) {
            this.cost = cost;
            return this;
        }
        public Builder setDeliveryAddress(DeliveryAddressDTO deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }
        public Builder setPickupPointName(String pickupPointName) {
            this.pickupPointName = pickupPointName;
            return this;
        }
        public Builder setPickupPointAddress(String pickupPointAddress) {
            this.pickupPointAddress = pickupPointAddress;
            return this;
        }
        public Builder setPickupPointLatitude(Double pickupPointLatitude) {
            this.pickupPointLatitude = pickupPointLatitude;
            return this;
        }
        public Builder setPickupPointLongitude(Double pickupPointLongitude) {
            this.pickupPointLongitude = pickupPointLongitude;
            return this;
        }

        public DeliveryMethodDTO build() {
            return new DeliveryMethodDTO(this);
        }
    }

    // Method for converting a DeliveryMethod into a DTO
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

        // Build the DTO with the Builder according to the type of delivery
        Builder builder = new Builder()
                .setId(deliveryMethod.getId())
                .setExpectedDeliveryDate(expectedDeliveryDate)
                .setCost(cost);

        if (deliveryMethod instanceof PickupPointDelivery pickupPoint) {
            builder.setDeliveryOption(DeliveryOption.PICKUP_POINT)
                    .setPickupPointName(pickupPoint.getPickupPointName())
                    .setPickupPointAddress(pickupPoint.getPickupPointAddress())
                    .setPickupPointLatitude(pickupPoint.getPickupPointLatitude())
                    .setPickupPointLongitude(pickupPoint.getPickupPointLongitude());
        } else if (deliveryMethod instanceof HomeStandardDelivery standardDelivery || deliveryMethod instanceof HomeExpressDelivery expressDelivery) {
            builder.setDeliveryOption(deliveryMethod instanceof HomeStandardDelivery ? DeliveryOption.HOME_STANDARD : DeliveryOption.HOME_EXPRESS)
                    .setDeliveryAddress(addressDTO);
        } else {
            throw new IllegalArgumentException("Type de méthode de livraison non pris en charge");
        }

        return builder.build();
    }
}
