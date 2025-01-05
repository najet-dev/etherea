package com.etherea.factories;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.*;

public class DeliveryMethodFactory {
    public static DeliveryMethod createDeliveryMethod(
            DeliveryOption option,
            DeliveryAddress deliveryAddress,
            String pickupPointName,
            String pickupPointAddress,
            Double latitude,
            Double longitude,
            User user
    ) {
        if (option == null) {
            throw new IllegalArgumentException("Delivery option must be specified.");
        }
        return switch (option) {
            case HOME_EXPRESS -> {
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("Delivery address required for express home delivery.");
                }
                yield new HomeExpressDelivery(deliveryAddress);
            }
            case HOME_STANDARD -> {
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("Delivery address required for standard home delivery.");
                }
                yield new HomeStandardDelivery(deliveryAddress);
            }
            case PICKUP_POINT -> {
                if (pickupPointName == null || pickupPointName.isBlank() ||
                        pickupPointAddress == null || pickupPointAddress.isBlank() ||
                        latitude == null || longitude == null ||
                        user == null) {
                    throw new IllegalArgumentException("The relay point information is incomplete.");
                }
                yield new PickupPointDelivery(pickupPointName, pickupPointAddress, latitude, longitude, user);
            }
            default -> throw new IllegalArgumentException("Delivery option not supported: " + option);
        };
    }
}
