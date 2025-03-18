package com.etherea.controllers;

import com.etherea.dtos.ForgotPasswordRequestDTO;
import com.etherea.dtos.ResetPasswordRequestDTO;
import com.etherea.repositories.ResetTokenRepository;
import com.etherea.services.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/resetToken")
@CrossOrigin(origins = "*")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;
    @Autowired
    private ResetTokenRepository resetTokenRepository;
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
