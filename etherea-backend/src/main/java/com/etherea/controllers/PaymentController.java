package com.etherea.controllers;

import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponseDTO;
import com.etherea.enums.PaymentStatus;
import com.etherea.services.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Route pour créer l'intention de paiement
    @PostMapping("/createPayment")
    public ResponseEntity<PaymentResponseDTO> createPaymentIntent(@RequestBody @Valid PaymentRequestDTO paymentRequestDTO) {
        try {
            PaymentResponseDTO response = paymentService.createPaymentIntent(paymentRequestDTO);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(new PaymentResponseDTO(null, null));
        }
    }

    // Route pour confirmer le paiement (en utilisant PathVariable pour récupérer paymentIntentId)
    @PostMapping("/confirm/{paymentIntentId}")
    public ResponseEntity<PaymentResponseDTO> confirmPayment(@PathVariable String paymentIntentId) {
        try {
            // Appel au service pour confirmer le paiement avec l'ID du paymentIntent
            PaymentResponseDTO response = paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(new PaymentResponseDTO(null, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new PaymentResponseDTO(PaymentStatus.FAILED, "Internal Server Error: " + e.getMessage()));
        }
    }
}
