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
                    throw new IllegalArgumentException("Delivery address is required for express home delivery.");
                }
                if (user == null) {
                    throw new IllegalArgumentException("User must be specified for express home delivery.");
                }
                yield new HomeExpressDelivery(deliveryAddress, user);
            }
            case HOME_STANDARD -> {
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("Delivery address is required for standard home delivery.");
                }
                if (user == null) {
                    throw new IllegalArgumentException("User must be specified for standard home delivery.");
                }
                yield new HomeStandardDelivery(deliveryAddress, user);
            }
            case PICKUP_POINT -> {
                if (pickupPointName == null || pickupPointName.isBlank() ||
                        pickupPointAddress == null || pickupPointAddress.isBlank() ||
                        latitude == null || longitude == null) {
                    throw new IllegalArgumentException("Relay point information is incomplete.");
                }
                if (user == null) {
                    throw new IllegalArgumentException("User must be specified for pickup point delivery.");
                }
                yield new PickupPointDelivery(
                        pickupPointName,
                        pickupPointAddress,
                        latitude,
                        longitude,
                        user
                );
            }
            default -> throw new IllegalArgumentException("Unsupported delivery option: " + option);
        };
    }
}
