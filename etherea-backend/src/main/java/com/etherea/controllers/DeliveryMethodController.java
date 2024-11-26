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
    @GetMapping("/options/{userId}")
    public List<DeliveryMethodDTO> getDeliveryOptions(@PathVariable Long userId) {
        return deliveryMethodService.getDeliveryOptions(userId);
    }

    /**
     * Récupère les points de collecte disponibles pour un utilisateur donné.
     */
    @GetMapping("/pickupPoints/{userId}")
    public ResponseEntity<List<AddDeliveryMethodRequestDTO>> getPickupPoints(@PathVariable Long userId) {
        List<AddDeliveryMethodRequestDTO> pickupPoints = pickupPointService.findPickupPoints(userId);
        return ResponseEntity.ok(pickupPoints);
    }

    /**
     * Calcule le total du panier en prenant en compte le coût de la livraison.
     */
    @GetMapping("/calculate-total")
    public double calculateTotal(@RequestParam double cartTotal, @RequestParam DeliveryOption selectedOption) {
        return deliveryMethodService.calculateTotal(cartTotal, selectedOption);
    }

    /**
     * Ajoute une méthode de livraison pour un utilisateur et une commande donnés.
     */
    @PostMapping("/add")
    public ResponseEntity<DeliveryMethodDTO> addDeliveryMethod(@RequestBody AddDeliveryMethodRequestDTO requestDTO) {
        DeliveryMethodDTO savedDeliveryMethod = deliveryMethodService.addDeliveryMethod(requestDTO);
        return ResponseEntity.ok(savedDeliveryMethod);
    }
}
