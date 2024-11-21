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

    /**
     * Récupère les options de livraison disponibles pour un utilisateur donné.
     */
    @GetMapping("/{userId}/options")
    public ResponseEntity<List<DeliveryMethodDTO>> getAvailableDeliveryMethods(@PathVariable Long userId) {
        List<DeliveryMethodDTO> options = deliveryMethodService.getAvailableDeliveryMethods(userId);
        return ResponseEntity.ok(options);
    }

    /**
     * Récupère les détails d'une méthode de livraison spécifique.
     */
    @GetMapping("/{userId}/{deliveryMethodId}")
    public ResponseEntity<DeliveryMethodDTO> getDeliveryMethod(
            @PathVariable Long userId,
            @PathVariable Long deliveryMethodId) {

        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.getDeliveryMethod(userId, deliveryMethodId);
        return ResponseEntity.ok(deliveryMethodDTO);
    }

    /**
     * Récupère les points de collecte disponibles pour un utilisateur donné.
     */
    @GetMapping("/{userId}/pickupPoints")
    public ResponseEntity<List<AddDeliveryMethodRequestDTO>> getPickupPoints(@PathVariable Long userId) {
        List<AddDeliveryMethodRequestDTO> pickupPoints = pickupPointService.findPickupPoints(userId);
        return ResponseEntity.ok(pickupPoints);
    }

    /**
     * Ajoute un mode de livraison choisi par l'utilisateur.
     */
    @PostMapping
    public ResponseEntity<DeliveryMethodDTO> addDeliveryMethod(@RequestBody AddDeliveryMethodRequestDTO request) {
        // Appel du service pour ajouter une méthode de livraison
        DeliveryMethodDTO deliveryMethodDTO = deliveryMethodService.addDeliveryMethod(request);
        return ResponseEntity.ok(deliveryMethodDTO);
    }
}
