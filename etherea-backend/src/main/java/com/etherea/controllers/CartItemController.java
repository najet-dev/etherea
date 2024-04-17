package com.etherea.controllers;

import com.etherea.dtos.CartItemDTO;
import com.etherea.exception.CartItemNotFoundException;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.models.User;
import com.etherea.repositories.CartItemRepository;
import com.etherea.repositories.ProductRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.services.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemDTO>> getUserCart(@PathVariable Long userId) {
        List<CartItemDTO> cartItemDTOs = cartItemService.getCartItemsByUserId(userId);
        return ResponseEntity.ok(cartItemDTOs);
    }
    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@RequestParam Long userId,
                                       @RequestParam Long productId,
                                       @RequestParam int quantity) {
        try {
            cartItemService.addProductToUserCart(userId, productId, quantity);
            return ResponseEntity.ok().body("{\"message\": \"Product added to cart successfully.\"}");
        } catch (UserNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An error occurred.\"}");
        }
    }
    @PutMapping("/{userId}/products/{productId}")
    public ResponseEntity<Map<String, String>> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam int newQuantity) {

        try {
            cartItemService.updateCartItemQuantity(userId, productId, newQuantity);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantité de l'élément de panier mise à jour avec succès");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException | ProductNotFoundException | CartItemNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        try {
            cartItemService.deleteCartItem(id);
            return ResponseEntity.noContent().build();
        } catch (CartItemNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
