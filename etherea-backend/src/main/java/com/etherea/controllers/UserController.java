package com.etherea.controllers;

import com.etherea.dtos.UpdateEmailRequestDTO;
import com.etherea.dtos.UpdatePasswordRequestDTO;
import com.etherea.dtos.UserDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UnauthorizedAccessException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.jwt.JwtUtils;
import com.etherea.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;
@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    public UserController(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of users per page (default is 10)
     * @return a paginated list of UserDTO objects, or HTTP 204 if empty
     */
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

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id the ID of the user
     * @return the corresponding UserDTO if found, or HTTP 404 with a message if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return a success message or an error message if the user is not found
     */
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

    /**
     * Updates a user's email address.
     *
     * @param updateEmailRequestDTO the DTO containing the new email and current credentials
     * @param token the JWT authentication token from the request header
     * @return a success message if the update is successful, or an error message otherwise
     */
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

    /**
     * Updates a user's password.
     *
     * @param updatePasswordRequestDTO the DTO containing the new password and current credentials
     * @param token the JWT authentication token from the request header
     * @return a success message if the update is successful, or an error message otherwise
     */
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