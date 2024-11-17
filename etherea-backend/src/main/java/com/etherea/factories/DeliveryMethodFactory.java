package com.etherea.factories;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.*;

public class DeliveryMethodFactory {
    public static DeliveryMethod createDeliveryMethod(
            DeliveryOption option,
            Double cartTotalAmount,
            DeliveryAddress deliveryAddress,
            String pickupPointName,
            String pickupPointAddress,
            Double latitude,
            Double longitude,
            User user
    ) {
        if (cartTotalAmount == null || cartTotalAmount < 0) {
            throw new IllegalArgumentException("The cart shopping amount must not be negative.");
        }
        switch (option) {
            case HOME_EXPRESS:
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("Delivery address required for express home delivery.");
                }
                return new HomeExpressDelivery(deliveryAddress);

            case HOME_STANDARD:
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("Delivery address required for standard home delivery.");
                }
                return new HomeStandardDelivery(deliveryAddress);

            case PICKUP_POINT:
                if (pickupPointName == null || pickupPointName.isEmpty() ||
                        pickupPointAddress == null || pickupPointAddress.isEmpty() ||
                        latitude == null || longitude == null ||
                        user == null) {
                    throw new IllegalArgumentException("The relay point information is incomplete.");
                }
                return new PickupPointDelivery(pickupPointName, pickupPointAddress, latitude, longitude, user);

            default:
                throw new IllegalArgumentException("Delivery option not supported : " + option);
        }
    }
}
