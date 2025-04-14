package com.etherea.controllers;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.services.DefaultAddressService;
import com.etherea.services.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deliveryAddresses")
@CrossOrigin
public class DeliveryAddressController {
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    @Autowired
    private DefaultAddressService defaultAddressService;


    /**
     * Retrieves all delivery addresses for a given user.
     *
     * @param userId the ID of the user.
     * @return ResponseEntity containing the list of DeliveryAddressDTOs.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<DeliveryAddressDTO>> getAllDeliveryAddresses(@PathVariable Long userId) {
        try {
            List<DeliveryAddressDTO> addresses = deliveryAddressService.getAllDeliveryAddresses(userId);
            return ResponseEntity.ok(addresses);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    /**
     * Retrieves a delivery address by user ID and address ID.
     *
     * @param userId the ID of the user.
     * @param addressId the ID of the delivery address.
     * @return ResponseEntity containing the DeliveryAddressDTO if found.
     */
    @GetMapping("/{userId}/{addressId}")
    public ResponseEntity<DeliveryAddressDTO> getDeliveryAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        DeliveryAddressDTO deliveryAddressDTO = deliveryAddressService.getDeliveryAddressByIdAndUserId(userId, addressId);
        return ResponseEntity.ok(deliveryAddressDTO);
    }
    /**
     * Adds a delivery address for a given user.
     *
     * @param userId the ID of the user.
     * @param deliveryAddressDTO the delivery address to add.
     * @return ResponseEntity containing a message and status of the operation.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<DeliveryAddressDTO> addDeliveryAddress(@PathVariable Long userId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        try {
            DeliveryAddressDTO newAddress = deliveryAddressService.addDeliveryAddress(userId, deliveryAddressDTO);
            return ResponseEntity.ok(newAddress);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    /**
     * Updates a delivery address for a given user.
     *
     * @param userId the ID of the user.
     * @param deliveryAddressDTO the delivery address to update.
     * @return ResponseEntity containing the updated DeliveryAddressDTO if successful.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<DeliveryAddressDTO> updateDeliveryAddress(@PathVariable Long userId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        try {
            DeliveryAddressDTO updatedAddress = deliveryAddressService.updateDeliveryAddress(userId, deliveryAddressDTO);
            return ResponseEntity.ok(updatedAddress);
        } catch (UserNotFoundException | DeliveryAddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    @PutMapping("/{userId}/{addressId}/set-default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
            defaultAddressService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException | DeliveryAddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    /**
     * Deletes a delivery address for a user.
     *
     * @param userId    The ID of the user.
     * @param addressId The ID of the address to be deleted.
     * @return ResponseEntity with the status of the operation.
     */
    @DeleteMapping("/{userId}/{addressId}")
    public ResponseEntity<Map<String, String>> deleteDeliveryAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
            deliveryAddressService.deleteDeliveryAddress(userId, addressId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Adresse supprimée avec succès.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | DeliveryAddressNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Erreur lors de la suppression de l'adresse.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}