package com.etherea.controllers;

import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.services.DeliveryMethodService;
import com.etherea.services.PickupPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveryMethods")
@CrossOrigin(origins = "*")
public class DeliveryMethodController {
    @Autowired
    private DeliveryMethodService deliveryMethodService;
    @Autowired
    private PickupPointService pickupPointService;

    @GetMapping("/{userId}/{deliveryMethodId}")
    public ResponseEntity<DeliveryMethodDTO> getDeliveryMethod(
            @PathVariable Long userId,
            @PathVariable Long deliveryMethodId) {

        // Appel au service pour récupérer la méthode de livraison existante
        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.getDeliveryMethod(userId, deliveryMethodId);

        return ResponseEntity.ok(deliveryMethodDTO);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<AddDeliveryMethodRequestDTO>> getPickupPoints(@PathVariable Long userId) {
        List<AddDeliveryMethodRequestDTO> pickupPoints = pickupPointService.findPickupPoints(userId);
        return ResponseEntity.ok(pickupPoints);
    }
    @PostMapping
    public ResponseEntity<DeliveryMethodDTO> addDeliveryMethod(@RequestBody AddDeliveryMethodRequestDTO request) {
        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.addDeliveryMethod(request);
        return ResponseEntity.ok(deliveryMethodDTO);
    }

}
