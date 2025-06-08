package com.etherea.controllers;

import com.etherea.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Long> getCartId(@PathVariable Long userId) {
        Long cartId = cartService.getCartIdByUserId(userId);
        return ResponseEntity.ok(cartId);
    }
}
