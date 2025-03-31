package com.etherea.controllers;

import com.etherea.dtos.UpdateEmailRequestDTO;
import com.etherea.dtos.UpdatePasswordRequestDTO;
import com.etherea.dtos.UserDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UnauthorizedAccessException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.jwt.JwtUtils;
import com.etherea.models.User;
import com.etherea.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    @Autowired
    private JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> usersPage = userService.getAllUsers(page, size);

        if (usersPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(usersPage);
    }
    @Autowired
    private UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (ProductNotFoundException e) {
            logger.error("User not found: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found with ID: " + id));
        }
    }
    @PutMapping("/update-email")
    public ResponseEntity<Map<String, String>> updateEmail(
            @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO,
            @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                throw new UnauthorizedAccessException("Token missing or incorrectly formatted");
            }

            String jwtToken = token.substring(7); // Remove “Bearer"
            if (!jwtUtils.validateJwtToken(jwtToken)) {
                throw new UnauthorizedAccessException("Invalid or expired JWT token");
            }

            // Call service with token
            boolean updated = userService.updateEmail(updateEmailRequestDTO, jwtToken);

            if (updated) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Email successfully updated"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Error updating email"));
            }

        } catch (UnauthorizedAccessException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }
    @PutMapping("/update-password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
            @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                throw new UnauthorizedAccessException("Missing or incorrectly formatted token");
            }

            String jwtToken = token.substring(7); // Remove “Bearer"
            if (!jwtUtils.validateJwtToken(jwtToken)) {
                throw new UnauthorizedAccessException("Invalid or expired JWT token");
            }

            boolean updated = userService.updatePassword(updatePasswordRequestDTO, jwtToken);

            if (updated) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Password successfully updated"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Error updating password"));
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