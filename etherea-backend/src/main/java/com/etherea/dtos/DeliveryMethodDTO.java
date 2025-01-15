package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.DeliveryMethodNotFoundException;
import com.etherea.models.*;
import com.etherea.utils.DeliveryDateCalculator;
import com.etherea.utils.DeliveryCostCalculator;

import java.time.LocalDate;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryOption deliveryOption;
    private LocalDate expectedDeliveryDate;
    private Double cost;
    private DeliveryAddressDTO deliveryAddress; // For home delivery
    private String pickupPointName;
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    private DeliveryMethodDTO(Builder builder) {
        // Delivery type-specific validation
        if (builder.deliveryOption == DeliveryOption.PICKUP_POINT) {
            if (builder.pickupPointName == null || builder.pickupPointAddress == null) {
                throw new DeliveryMethodNotFoundException("Les informations du point de collecte sont manquantes pour une livraison en point relais.");
            }
        } else if (builder.deliveryOption == DeliveryOption.HOME_STANDARD || builder.deliveryOption == DeliveryOption.HOME_EXPRESS) {
            if (builder.deliveryAddress == null) {
                throw new DeliveryAddressNotFoundException("L'adresse de livraison est obligatoire pour une livraison à domicile.");
            }
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

    // Builder class
    public static class Builder {
        private Long id;
        private DeliveryOption deliveryOption;
        private LocalDate expectedDeliveryDate;
        private Double cost;
        private DeliveryAddressDTO deliveryAddress;
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

    // Conversion method
    public static DeliveryMethodDTO fromDeliveryMethod(
            DeliveryMethod deliveryMethod,
            DeliveryAddress userAddress,
            LocalDate startDate,
            double orderAmount,
            DeliveryDateCalculator calculator
    ) {
        if (deliveryMethod == null || calculator == null) {
            throw new IllegalArgumentException("Les paramètres deliveryMethod et calculator ne peuvent pas être nuls.");
        }

        DeliveryAddressDTO addressDTO = userAddress != null ? DeliveryAddressDTO.fromDeliveryAddress(userAddress) : null;

        LocalDate expectedDeliveryDate = calculator.calculateDeliveryDate(startDate, deliveryMethod.calculateDeliveryTime());

        Double cost = DeliveryCostCalculator.calculateDeliveryCost(orderAmount, deliveryMethod.getDeliveryOption());

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
        } else if (deliveryMethod instanceof HomeStandardDelivery || deliveryMethod instanceof HomeExpressDelivery) {
            builder.setDeliveryOption(
                            deliveryMethod instanceof HomeStandardDelivery ? DeliveryOption.HOME_STANDARD : DeliveryOption.HOME_EXPRESS)
                    .setDeliveryAddress(addressDTO);
        } else {
            throw new IllegalArgumentException("Type de méthode de livraison non pris en charge : " + deliveryMethod.getClass().getSimpleName());
        }

        return builder.build();
    }
}