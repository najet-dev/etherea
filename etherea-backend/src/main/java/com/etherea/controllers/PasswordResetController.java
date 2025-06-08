package com.etherea.controllers;

import com.etherea.dtos.ForgotPasswordRequestDTO;
import com.etherea.dtos.ResetPasswordRequestDTO;
import com.etherea.repositories.ResetTokenRepository;
import com.etherea.services.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/resetToken")
@CrossOrigin(origins = "*")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    public final ResetTokenRepository resetTokenRepository;
    public PasswordResetController (PasswordResetService passwordResetService, ResetTokenRepository resetTokenRepository) {
        this.passwordResetService = passwordResetService;
        this.resetTokenRepository = resetTokenRepository;
    }
    /**
     * Sends a password reset link to the user's email address.
     *
     * @param request the request object containing the user's email
     * @return a ResponseEntity with a success message if the email was sent,
     *         or an error message if an exception occurred
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Validated @RequestBody ForgotPasswordRequestDTO request) {
        try {
            passwordResetService.sendPasswordResetLink(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Un email de réinitialisation a été envoyé.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de l'envoi de l'email. Veuillez réessayer.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    /**
     * Resets the user's password using the provided reset token and new password.
     *
     * @param request the request object containing the reset token and new password
     * @return a ResponseEntity with a success message if the password was reset,
     *         or an error message if the token is invalid or expired
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Validated @RequestBody ResetPasswordRequestDTO request) {
        try {
            passwordResetService.resetPassword(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mot de passe réinitialisé avec succès.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
