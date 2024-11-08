package com.etherea.factories;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.models.DeliveryAddress;
import com.etherea.models.HomeExpressDelivery;
import com.etherea.models.HomeStandardDelivery;
import com.etherea.models.PickupPointDelivery;

public class DeliveryMethodFactory {

    public static DeliveryMethod createDeliveryMethod(
            DeliveryOption option,
            Double orderAmount,
            DeliveryAddress deliveryAddress,
            String pickupPointName,
            String pickupPointAddress,
            Double latitude,
            Double longitude
    ) {
        if (orderAmount == null || orderAmount < 0) {
            throw new IllegalArgumentException("Le montant de la commande doit être non négatif.");
        }

        switch (option) {
            case HOME_EXPRESS:
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("L'adresse de livraison est requise pour la livraison express.");
                }
                return new HomeExpressDelivery(deliveryAddress);

            case HOME_STANDARD:
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("L'adresse de livraison est requise pour la livraison standard.");
                }
                return new HomeStandardDelivery(deliveryAddress);

            case PICKUP_POINT:
                if (pickupPointName == null || pickupPointName.isEmpty() ||
                        pickupPointAddress == null || pickupPointAddress.isEmpty()) {
                    throw new IllegalArgumentException("Le nom et l'adresse sont requis pour la livraison au point de retrait.");
                }
                if (latitude == null || longitude == null || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                    throw new IllegalArgumentException("Les coordonnées de latitude et longitude doivent être valides.");
                }
                return new PickupPointDelivery(pickupPointName, pickupPointAddress, latitude, longitude);

            default:
                throw new IllegalArgumentException("Option de livraison non supportée : " + option);
        }
    }
}
