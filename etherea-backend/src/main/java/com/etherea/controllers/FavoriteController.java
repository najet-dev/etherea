package com.etherea.controllers;

import com.etherea.dtos.FavoriteDTO;
import com.etherea.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getUserFavorites(@PathVariable Long userId) {
        try {
            List<FavoriteDTO> userFavorites = favoriteService.getUserFavorites(userId);
            return ResponseEntity.ok(userFavorites);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("/{userId}/{productId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            favoriteService.addFavorite(userId, productId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product added to favorites successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or product not found");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product already in favorites");
        }
    }
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            favoriteService.removeFavorite(userId, productId);
            return ResponseEntity.ok("Product removed from favorites successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or product not found");
        }
    }
}
