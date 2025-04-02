package com.etherea.controllers;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.services.DeliveryAddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveryAddresses")
@CrossOrigin
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    public DeliveryAddressController(DeliveryAddressService deliveryAddressService) {
        this.deliveryAddressService = deliveryAddressService;
    }

    /**
     * Retrieves all delivery addresses for a given user.
     *
     * @param userId the ID of the user.
     * @return ResponseEntity containing the list of DeliveryAddressDTOs.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<DeliveryAddressDTO>> getAllDeliveryAddresses(@PathVariable Long userId) {
        List<DeliveryAddressDTO> addresses = deliveryAddressService.getAllDeliveryAddresses(userId);
        return ResponseEntity.ok(addresses);
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
     * @return ResponseEntity containing the created DeliveryAddressDTO.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<DeliveryAddressDTO> addDeliveryAddress(@PathVariable Long userId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        DeliveryAddressDTO newAddress = deliveryAddressService.addDeliveryAddress(userId, deliveryAddressDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
    }

    /**
     * Updates a delivery address for a given user.
     *
     * @param userId the ID of the user.
     * @param deliveryAddressDTO the delivery address to update (must contain the address ID).
     * @return ResponseEntity containing the updated DeliveryAddressDTO.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<DeliveryAddressDTO> updateDeliveryAddress(@PathVariable Long userId, @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        if (deliveryAddressDTO.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        DeliveryAddressDTO updatedAddress = deliveryAddressService.updateDeliveryAddress(userId, deliveryAddressDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * Handles exceptions for UserNotFoundException and DeliveryAddressNotFoundException.
     */
    @ExceptionHandler({UserNotFoundException.class, DeliveryAddressNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Handles generic exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Une erreur s'est produite: " + e.getMessage());
    }
}
