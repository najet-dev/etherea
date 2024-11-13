package com.etherea.factories;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.models.DeliveryAddress;
import com.etherea.models.HomeExpressDelivery;
import com.etherea.models.HomeStandardDelivery;
import com.etherea.models.PickupPointDelivery;
import com.etherea.models.User;

public class DeliveryMethodFactory {

    public static DeliveryMethod createDeliveryMethod(
            DeliveryOption option,
            Double orderAmount,
            DeliveryAddress deliveryAddress,
            String pickupPointName,
            String pickupPointAddress,
            Double latitude,
            Double longitude,
            User user // Ajout du paramètre `user`
    ) {
        if (orderAmount == null || orderAmount < 0) {
            throw new IllegalArgumentException("Le montant de la commande doit être non négatif.");
        }

        switch (option) {
            case HOME_EXPRESS:
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("L'adresse de livraison est requise pour la livraison express.");
                }
                HomeExpressDelivery expressDelivery = new HomeExpressDelivery(deliveryAddress);
                // Application des frais de livraison en fonction du montant de la commande
                expressDelivery.calculateCost(orderAmount);
                return expressDelivery;

            case HOME_STANDARD:
                if (deliveryAddress == null) {
                    throw new IllegalArgumentException("L'adresse de livraison est requise pour la livraison standard.");
                }
                HomeStandardDelivery standardDelivery = new HomeStandardDelivery(deliveryAddress);
                // Application des frais de livraison en fonction du montant de la commande
                standardDelivery.calculateCost(orderAmount);
                return standardDelivery;

            case PICKUP_POINT:
                if (pickupPointName == null || pickupPointName.isEmpty() ||
                        pickupPointAddress == null || pickupPointAddress.isEmpty()) {
                    throw new IllegalArgumentException("Le nom et l'adresse sont requis pour la livraison au point de retrait.");
                }
                if (latitude == null || longitude == null || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                    throw new IllegalArgumentException("Les coordonnées de latitude et longitude doivent être valides.");
                }
                if (user == null) {
                    throw new IllegalArgumentException("L'utilisateur est requis pour la livraison en point relais.");
                }
                PickupPointDelivery pickupDelivery = new PickupPointDelivery(pickupPointName, pickupPointAddress, latitude, longitude, user);
                // Application des frais de livraison en fonction du montant de la commande
                pickupDelivery.calculateCost(orderAmount);
                return pickupDelivery;

            default:
                throw new IllegalArgumentException("Option de livraison non supportée : " + option);
        }
    }
}
