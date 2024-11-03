package com.etherea.controllers;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.services.DeliveryMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-methods")
@CrossOrigin
public class DeliveryMethodController {

    private final DeliveryMethodService deliveryMethodService;

    @Autowired
    public DeliveryMethodController(DeliveryMethodService deliveryMethodService) {
        this.deliveryMethodService = deliveryMethodService;
    }

    @GetMapping("/nearest")
    public ResponseEntity<?> getAvailableDeliveryMethods(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1000") double radius,
            @RequestParam(defaultValue = "0.0") Double orderAmount) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("L'ID de l'utilisateur doit être fourni.");
        }
        if (radius <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Le rayon doit être supérieur à 0.");
        }

        try {
            // Récupère une liste de toutes les méthodes de livraison disponibles
            List<DeliveryMethodDTO> deliveryMethods = deliveryMethodService.getAvailableDeliveryMethods(userId, radius, orderAmount);
            return ResponseEntity.ok(deliveryMethods);

        } catch (DeliveryAddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucune adresse de livraison trouvée pour l'utilisateur avec ID : " + userId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des méthodes de livraison : " + e.getMessage());
        }
    }
}
