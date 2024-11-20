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

    @GetMapping("/{userId}/available")
    public ResponseEntity<List<DeliveryMethodDTO>> getAvailableDeliveryMethods(@PathVariable Long userId) {
        List<DeliveryMethodDTO> deliveryMethods = deliveryMethodService.getAllDeliveryMethods(userId);
        return ResponseEntity.ok(deliveryMethods);
    }

    @GetMapping("/{userId}/{deliveryMethodId}")
    public ResponseEntity<DeliveryMethodDTO> getDeliveryMethod(
            @PathVariable Long userId,
            @PathVariable Long deliveryMethodId) {

        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.getDeliveryMethod(userId, deliveryMethodId);
        return ResponseEntity.ok(deliveryMethodDTO);
    }

    // Récupération des points de collecte disponibles
    @GetMapping("/{userId}/pickupPoints")
    public ResponseEntity<List<AddDeliveryMethodRequestDTO>> getPickupPoints(@PathVariable Long userId) {
        List<AddDeliveryMethodRequestDTO> pickupPoints = pickupPointService.findPickupPoints(userId);
        return ResponseEntity.ok(pickupPoints);
    }
    @PostMapping
    public ResponseEntity<DeliveryMethodDTO> addDeliveryMethod(@RequestBody AddDeliveryMethodRequestDTO request) {
        // Validation des informations nécessaires pour la livraison en point relais
        if (request.getDeliveryOption() == DeliveryOption.PICKUP_POINT) {
            if (request.getPickupPointName() == null || request.getPickupPointAddress() == null ||
                    request.getPickupPointLatitude() == null || request.getPickupPointLongitude() == null) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        // Appel du service pour ajouter une méthode de livraison
        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.addDeliveryMethod(request);
        return ResponseEntity.ok(deliveryMethodDTO);
    }
}
