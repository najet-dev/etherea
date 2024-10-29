package com.etherea.services;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.dtos.PickupPointDTO;
import com.etherea.enums.DeliveryOption;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryService {

    private final PickupPointService pickupPointService;

    public DeliveryService(PickupPointService pickupPointService) {
        this.pickupPointService = pickupPointService;
    }

    public DeliveryMethodDTO calculateDeliveryMethod(DeliveryOption option, Long userId, double radius) {
        DeliveryMethodDTO deliveryMethodDTO = new DeliveryMethodDTO();
        deliveryMethodDTO.setDeliveryOption(option);

        switch (option) {
            case PICKUP_POINT:
                // Appelez la méthode avec l'ID utilisateur et le rayon
                List<PickupPointDTO> points = pickupPointService.findNearestPickupPoints(userId, radius);
                deliveryMethodDTO.setPickupPoints(points);
                deliveryMethodDTO.setCost(3.0); // Coût pour le point relais
                deliveryMethodDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(3));
                break;

            case HOME_STANDARD:
                deliveryMethodDTO.setCost(5.0); // Coût pour la livraison standard
                deliveryMethodDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
                break;

            case HOME_EXPRESS:
                deliveryMethodDTO.setCost(10.0); // Coût pour la livraison express
                deliveryMethodDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(1));
                break;
        }

        return deliveryMethodDTO;
    }
}
