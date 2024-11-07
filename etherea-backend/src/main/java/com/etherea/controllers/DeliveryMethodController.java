package com.etherea.controllers;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.services.DeliveryMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveryMethods")
public class DeliveryMethodController {

    @Autowired
    private DeliveryMethodService deliveryMethodService;

    @PostMapping
    public ResponseEntity<DeliveryMethodDTO> addDeliveryMethod(
            @RequestParam Long userId,
            @RequestParam DeliveryOption deliveryOption,
            @RequestParam(required = false) String pickupPointName,
            @RequestParam(required = false) String pickupPointAddress,
            @RequestParam(required = false) Double pickupPointLatitude,
            @RequestParam(required = false) Double pickupPointLongitude,
            @RequestParam double orderAmount) {

        // Appel au service pour ajouter le mode de livraison
        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.addDeliveryMethod(
                userId, deliveryOption, pickupPointName, pickupPointAddress,
                pickupPointLatitude, pickupPointLongitude, orderAmount
        );

        return ResponseEntity.ok(deliveryMethodDTO);
    }
}
