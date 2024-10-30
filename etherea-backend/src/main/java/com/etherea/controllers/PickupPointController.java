package com.etherea.controllers;

import com.etherea.dtos.PickupPointDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.services.PickupPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/pickupPoints")
public class PickupPointController {
    private final PickupPointService pickupPointService;

    @Autowired
    public PickupPointController(PickupPointService pickupPointService) {
        this.pickupPointService = pickupPointService;
    }

    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestPickupPoints(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1000") double radius) {  // Rayon par défaut de 1000 mètres

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("L'ID de l'utilisateur doit être fourni.");
        }
        if (radius <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Le rayon doit être supérieur à 0.");
        }

        try {
            List<PickupPointDTO> pickupPoints = pickupPointService.findNearestPickupPoints(userId, radius);
            return ResponseEntity.ok(pickupPoints);

        } catch (DeliveryAddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucune adresse de livraison trouvée pour l'utilisateur avec ID : " + userId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des points relais : " + e.getMessage());
        }
    }
}
