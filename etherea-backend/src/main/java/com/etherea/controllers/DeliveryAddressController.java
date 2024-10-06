package com.etherea.controllers;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.services.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/deliveryAddresses")
@CrossOrigin
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    /**
     * Ajoute une adresse de livraison pour un utilisateur donné.
     *
     * @param userId l'ID de l'utilisateur
     * @param deliveryAddressDTO l'adresse de livraison à ajouter
     * @return une réponse HTTP indiquant le résultat de l'opération
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> addDeliveryAddress(@PathVariable Long userId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            deliveryAddressService.addDeliveryAddress(userId, deliveryAddressDTO);
            response.put("message", "Adresse de livraison ajoutée avec succès.");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Erreur lors de l'ajout de l'adresse de livraison : " + e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
