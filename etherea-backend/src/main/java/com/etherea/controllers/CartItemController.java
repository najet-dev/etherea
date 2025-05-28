package com.etherea.controllers;

import com.etherea.dtos.CartItemDTO;
import com.etherea.exception.CartItemNotFoundException;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.exception.VolumeNotFoundException;
import com.etherea.services.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cartItem")
@CrossOrigin
public class CartItemController {
    private final CartItemService cartItemService;
    private static final Logger logger = LoggerFactory.getLogger(CartItemController.class);
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    /**
     * Get all items in a user's cart.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getUserCart(@PathVariable Long userId) {
        try {
            List<CartItemDTO> cartItemDTOs = cartItemService.getCartItemsByUserId(userId);
            logger.info("Cart items retrieved for user {}: {}", userId, cartItemDTOs);
            return ResponseEntity.ok(cartItemDTOs);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Add a product to the user's cart.
     */
    @PostMapping("/addToCart")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody CartItemDTO cartItemDTO) {
        if (cartItemDTO.getUserId() == null) {
            logger.error("User ID is null in addToCart");
            Map<String, String> response = new HashMap<>();
            response.put("error", "User ID must not be null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            cartItemService.addProductToUserCart(cartItemDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product added to cart successfully.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | ProductNotFoundException | VolumeNotFoundException e) {
            logger.error("Error adding product to cart: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument error: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update the quantity of a HAIR product in the cart.
     */
    @PutMapping("/updateProductHair")
    public ResponseEntity<Map<String, String>> updateCartItemQuantityForHair(@RequestBody CartItemDTO cartItemDTO) {
        try {
            cartItemService.updateCartItemQuantity(cartItemDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantity of HAIR product cart item updated successfully.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | ProductNotFoundException | CartItemNotFoundException | VolumeNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Stock issue: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update the quantity of a FACE product in the cart.
     */
    @PutMapping("/updateProductFace")
    public ResponseEntity<Map<String, String>> updateCartItemQuantityForFace(@RequestBody CartItemDTO cartItemDTO) {
        try {
            cartItemService.updateCartItemQuantity(cartItemDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantity of FACE product cart item updated successfully.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | ProductNotFoundException | CartItemNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid quantity: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a cart item from the user's cart.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCartItem(@PathVariable Long id) {
        try {
            cartItemService.deleteCartItem(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart item deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (CartItemNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
