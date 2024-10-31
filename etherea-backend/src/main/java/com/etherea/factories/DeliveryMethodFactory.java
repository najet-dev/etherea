package com.etherea.factories;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.models.HomeExpressDelivery;
import com.etherea.models.HomeStandardDelivery;
import com.etherea.models.PickupPointDelivery;

public class DeliveryMethodFactory {

    /**
     * Creates a DeliveryMethod based on the specified DeliveryOption.
     * @param option The type of delivery option to create.
     * @return An instance of the appropriate DeliveryMethod.
     * @throws IllegalArgumentException if the delivery option is unsupported.
     */
    public static DeliveryMethod createDeliveryMethod(DeliveryOption option) {
        return switch (option) {
            case HOME_EXPRESS -> new HomeExpressDelivery();
            case HOME_STANDARD -> new HomeStandardDelivery();
            case PICKUP_POINT -> new PickupPointDelivery();
            default -> throw new IllegalArgumentException("Unsupported delivery option: " + option);
        };
    }

    /**
     * Overloaded factory method to create PickupPointDelivery with additional parameters.
     * @param option The type of delivery option to create.
     * @param name The name of the pickup point (for PICKUP_POINT only).
     * @param address The address of the pickup point (for PICKUP_POINT only).
     * @param latitude Latitude for the pickup point location.
     * @param longitude Longitude for the pickup point location.
     * @return An instance of the appropriate DeliveryMethod.
     * @throws IllegalArgumentException if the delivery option or parameters are unsupported.
     */
    public static DeliveryMethod createDeliveryMethod(DeliveryOption option, String name, String address, Double latitude, Double longitude) {
        if (option == DeliveryOption.PICKUP_POINT) {
            return new PickupPointDelivery(name, address, latitude, longitude);
        } else {
            throw new IllegalArgumentException("Additional parameters only supported for PICKUP_POINT delivery option.");
        }
    }
}
