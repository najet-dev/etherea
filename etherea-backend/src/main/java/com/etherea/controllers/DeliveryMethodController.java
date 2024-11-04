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
    @Autowired private DeliveryMethodService deliveryMethodService;
    @GetMapping("/nearest")
    public ResponseEntity<?> getAvailableDeliveryMethods(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1000") double radius,
            @RequestParam(defaultValue = "0.0") Double orderAmount) {

        try {
            List<DeliveryMethodDTO> deliveryMethods = deliveryMethodService.getAvailableDeliveryMethods(userId, radius, orderAmount);
            return ResponseEntity.ok(deliveryMethods);

        } catch (DeliveryAddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching delivery methods.");
        }
    }
}
