package com.etherea.controllers;

import com.etherea.dtos.FavoriteDTO;
import com.etherea.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable Long userId) {
        try {
            List<FavoriteDTO> userFavorites = favoriteService.getUserFavorites(userId);
            return ResponseEntity.ok(userFavorites);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @PostMapping("/{userId}/{productId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            favoriteService.addFavorite(userId, productId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product added to favorites successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User or product not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Product already in favorites");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateFavorites(@PathVariable Long userId, @RequestBody List<Long> updatedProductIds) {
        try {
            favoriteService.updateFavorites(userId, updatedProductIds);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Favorites updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            favoriteService.removeFavorite(userId, productId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product removed from favorites successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User or product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
