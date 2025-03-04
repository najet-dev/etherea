package com.etherea.controllers;

import com.etherea.dtos.UpdateEmailRequestDTO;
import com.etherea.dtos.UserDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UnauthorizedAccessException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.jwt.JwtUtils;
import com.etherea.models.User;
import com.etherea.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }
    }
    @PutMapping("/update-email")
    public ResponseEntity<Map<String, String>> updateEmail(
            @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO,
            @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                throw new UnauthorizedAccessException("Token manquant ou mal formaté.");
            }

            String jwtToken = token.substring(7); // Enlever "Bearer "
            if (!jwtUtils.validateJwtToken(jwtToken)) {
                throw new UnauthorizedAccessException("Token JWT invalide ou expiré.");
            }

            // Appel du service avec le token
            boolean updated = userService.updateEmail(updateEmailRequestDTO, jwtToken);

            if (updated) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Email mis à jour avec succès"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Erreur lors de la mise à jour de l'email"));
            }

        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }
}
