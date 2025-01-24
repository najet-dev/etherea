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
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody @Valid PaymentRequestDTO paymentRequestDTO) {
        try {
            PaymentResponseDTO response = paymentService.processPayment(paymentRequestDTO);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(new PaymentResponseDTO(PaymentStatus.FAILED, null));
        }
    }
}
