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

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping("/createPayment")
    public ResponseEntity<PaymentResponseDTO> createPaymentIntent(@RequestBody @Valid PaymentRequestDTO paymentRequestDTO) {
        try {
            PaymentResponseDTO response = paymentService.createPaymentIntent(paymentRequestDTO);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(new PaymentResponseDTO(null, null));
        }
    }
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDTO> confirmPayment(@RequestBody Map<String, String> request) {
        try {
            String paymentIntentId = request.get("paymentIntentId");
            String paymentMethodId = request.get("paymentMethodId");

            PaymentResponseDTO response = paymentService.confirmPayment(paymentIntentId, paymentMethodId);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(new PaymentResponseDTO(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new PaymentResponseDTO(PaymentStatus.FAILED, "Internal Server Error: " + e.getMessage()));
        }
    }
}
