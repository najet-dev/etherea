package com.etherea.factories;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.models.HomeExpressDelivery;
import com.etherea.models.HomeStandardDelivery;
import com.etherea.models.PickupPointDelivery;

public class DeliveryMethodFactory {

    /**
     * Creates a DeliveryMethod instance based on the specified DeliveryOption.
     *
     * @param option The delivery option type to create.
     * @param orderAmount The order amount to calculate delivery cost.
     * @return An instance of the appropriate DeliveryMethod.
     * @throws IllegalArgumentException if the delivery option is unsupported.
     */
    public static DeliveryMethod createDeliveryMethod(DeliveryOption option, Double orderAmount) {
        switch (option) {
            case HOME_EXPRESS:
                return new HomeExpressDelivery(orderAmount);
            case HOME_STANDARD:
                return new HomeStandardDelivery(orderAmount);
            case PICKUP_POINT:
                throw new IllegalArgumentException("For PICKUP_POINT, please provide pickup location details.");
            default:
                throw new IllegalArgumentException("Unsupported delivery option: " + option);
        }
    }

    /**
     * Creates a PickupPointDelivery with additional details about the pickup location.
     *
     * @param option The delivery option type to create.
     * @param name The name of the pickup point (for PICKUP_POINT only).
     * @param address The address of the pickup point (for PICKUP_POINT only).
     * @param latitude Latitude for the pickup point location.
     * @param longitude Longitude for the pickup point location.
     * @param orderAmount The order amount to calculate delivery cost.
     * @return An instance of PickupPointDelivery initialized with the specified details.
     * @throws IllegalArgumentException if the delivery option is not PICKUP_POINT or parameters are invalid.
     */
    public static DeliveryMethod createPickupPointDelivery(
            DeliveryOption option,
            String name,
            String address,
            Double latitude,
            Double longitude,
            Double orderAmount
    ) {
        if (option != DeliveryOption.PICKUP_POINT) {
            throw new IllegalArgumentException("Additional parameters are only supported for PICKUP_POINT delivery option.");
        }
        if (name == null || name.isEmpty() || address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Name and address are required for pickup point delivery.");
        }
        return new PickupPointDelivery(name, address, latitude, longitude, orderAmount);
    }
}
