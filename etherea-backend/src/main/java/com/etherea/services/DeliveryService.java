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
                List<PickupPointDTO> points = pickupPointService.findNearestPickupPoints(userId, radius);
                deliveryMethodDTO.setPickupPoints(points);
                deliveryMethodDTO.setCost(3.0);
                deliveryMethodDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(3));
                break;

            case HOME_STANDARD:
                deliveryMethodDTO.setCost(5.0);
                deliveryMethodDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
                break;

            case HOME_EXPRESS:
                deliveryMethodDTO.setCost(10.0);
                deliveryMethodDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(1));
                break;

            default:
                throw new IllegalArgumentException("Option de livraison inconnue : " + option);
        }
        return deliveryMethodDTO;
    }
}
