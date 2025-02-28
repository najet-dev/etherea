package com.etherea.controllers;

import com.etherea.dtos.*;
import com.etherea.enums.DeliveryName;
import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.DeliveryType;
import com.etherea.services.DeliveryMethodService;
import com.etherea.services.PickupPointService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;


import java.util.List;

@RestController
@RequestMapping("/deliveryMethods")
@CrossOrigin(origins = "*")
public class DeliveryMethodController {
    @Autowired
    private DeliveryMethodService deliveryMethodService;
    @Autowired
    private PickupPointService pickupPointService;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryMethodController.class);

    /**
     * Retrieves available delivery options for a given user.
     *
     * @param userId The ID of the user.
     * @return List of available delivery options as DeliveryTypeDTO objects.
     */
    @GetMapping("/options/{userId}")
    public List<DeliveryTypeDTO> getDeliveryOptions(@PathVariable Long userId) {
        logger.info("Request received with userId: {}", userId);

        return deliveryMethodService.getDeliveryOptions(userId);
    }

    /**
     * Retrieves available pickup points for a given user.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing a list of AddDeliveryMethodRequestDTO representing pickup points.
     */
    @GetMapping("/pickupPoints/{userId}")
    public ResponseEntity<List<AddDeliveryMethodRequestDTO>> getPickupPoints(@PathVariable Long userId) {
        List<AddDeliveryMethodRequestDTO> pickupPoints = pickupPointService.findPickupPoints(userId);
        return ResponseEntity.ok(pickupPoints);
    }

    /**
     * Retrieves the total amount of the user's cart.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing the total amount of the cart as BigDecimal.
     */
    @GetMapping("/cart-total/{userId}")
    public ResponseEntity<BigDecimal> getCartTotal(@PathVariable Long userId) {
        BigDecimal cartTotal = deliveryMethodService.getCartTotal(userId);
        return ResponseEntity.ok(cartTotal);
    }

    /**
     * Calculates the total cart amount including delivery cost.
     *
     * @param userId       The ID of the user.
     * @param selectedType The selected delivery type as DeliveryName enum.
     * @return ResponseEntity containing CartWithDeliveryDTO with total amount including delivery.
     */
    @GetMapping("/cart-with-delivery/{userId}")
    public ResponseEntity<CartWithDeliveryDTO> getCartWithDelivery(
            @PathVariable Long userId,
            @RequestParam DeliveryName selectedType) {
        CartWithDeliveryDTO response = deliveryMethodService.getCartWithDeliveryTotal(userId, selectedType);
        logger.info("Type of delivery received: {}", selectedType);

        return ResponseEntity.ok(response);
    }

    /**
     * Adds a delivery method for a given user and order.
     *
     * @param requestDTO DTO containing details of the delivery method to be added.
     * @return ResponseEntity with saved DeliveryMethodDTO if successful, or error message if not found or validation fails.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addDeliveryMethod(@Valid @RequestBody AddDeliveryMethodRequestDTO requestDTO) {
        try {
            DeliveryMethodDTO savedDeliveryMethod = deliveryMethodService.addDeliveryMethod(requestDTO);
            return ResponseEntity.ok(savedDeliveryMethod);
        } catch (UserNotFoundException | CartNotFoundException | DeliveryAddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Internal error while adding delivery method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An internal server error occurred."));
        }
    }

    /**
     * Updates a delivery method for a given deliveryMethodId.
     *
     * @param deliveryMethodId The ID of the delivery method to update.
     * @param requestDTO       DTO containing updated details of the delivery method.
     * @return ResponseEntity with updated DeliveryMethodDTO if successful, or error message if not found or validation fails.
     */
    @PutMapping("/update/{deliveryMethodId}")
    public ResponseEntity<?> updateDeliveryMethod(@PathVariable Long deliveryMethodId, @RequestBody AddDeliveryMethodRequestDTO requestDTO) {
        try {
            DeliveryMethodDTO updatedDeliveryMethod = deliveryMethodService.updateDeliveryMethod(deliveryMethodId, requestDTO);
            return ResponseEntity.ok(updatedDeliveryMethod);
        } catch (UserNotFoundException | CartNotFoundException | DeliveryAddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An internal server error occurred."));
        }
    }
}