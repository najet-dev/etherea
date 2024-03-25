package com.etherea.controllers;

import com.etherea.dtos.CartItemDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.CartItem;
import com.etherea.models.User;
import com.etherea.repositories.CartItemRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.services.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
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
    public ResponseEntity<String> addToCart(@RequestParam Long userId,
                                            @RequestParam Long productId,
                                            @RequestParam int quantity) {
        try {
            cartItemService.addProductToUserCart(userId, productId, quantity);
            return ResponseEntity.ok("Product added to cart successfully.");
        } catch (UserNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
    @GetMapping("/total/{userId}")
    public double getCartTotal(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return cartItemService.calculateCartTotal(user.getId());
    }
}
