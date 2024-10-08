package com.etherea.controllers;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.models.DeliveryAddress;
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
     * Retrieves a delivery address by user ID and address ID.
     *
     * @param userId the ID of the user.
     * @param id     the ID of the delivery address.
     * @return ResponseEntity containing the DeliveryAddressDTO if found.
     */
    @GetMapping("/{userId}/{id}")
    public ResponseEntity<DeliveryAddressDTO> getDeliveryAddress(@PathVariable Long userId, @PathVariable Long id) {

        DeliveryAddressDTO deliveryAddressDTO = deliveryAddressService.getDeliveryAddressByIdAndUserId(userId, id);

        return ResponseEntity.ok(deliveryAddressDTO);
    }
    /**
     * Adds a delivery address for a given user.
     *
     * @param userId             the ID of the user.
     * @param deliveryAddressDTO the delivery address to add.
     * @return ResponseEntity containing a message and status of the operation.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> addDeliveryAddress(@PathVariable Long userId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            deliveryAddressService.addDeliveryAddress(userId, deliveryAddressDTO);
            response.put("message", "Delivery address added successfully.");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error adding delivery address: " + e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
