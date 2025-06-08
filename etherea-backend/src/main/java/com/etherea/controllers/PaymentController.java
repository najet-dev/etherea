package com.etherea.controllers;

import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponseDTO;
import com.etherea.enums.PaymentStatus;
import com.etherea.services.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Creates a new Stripe payment intent based on the payment request details and user ID.
     *
     * @param paymentRequestDTO the payment request data including amount and currency
     * @param userId the ID of the user initiating the payment
     * @return a ResponseEntity containing the payment response with status and client secret if successful,
     *         or an error response if the creation fails
     */
    @PostMapping("/createPayment")
    public ResponseEntity<PaymentResponseDTO> createPaymentIntent(@RequestBody @Valid PaymentRequestDTO paymentRequestDTO, Long userId) {
        try {
            PaymentResponseDTO response = paymentService.createPaymentIntent(paymentRequestDTO, userId);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Stripe error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaymentResponseDTO(PaymentStatus.FAILED, e.getMessage(), null));
        }
    }
    /**
     * Confirms a Stripe payment intent using the provided payment intent ID and payment method ID.
     *
     * @param request a map containing the paymentIntentId and paymentMethodId
     * @return a ResponseEntity containing the payment response with status if successful,
     *         or an error response if confirmation fails
     */
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDTO> confirmPayment(@RequestBody Map<String, String> request) {
        try {
            String paymentIntentId = request.get("paymentIntentId");
            String paymentMethodId = request.get("paymentMethodId");

            PaymentResponseDTO response = paymentService.confirmPayment(paymentIntentId, paymentMethodId);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(new PaymentResponseDTO(null, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new PaymentResponseDTO(PaymentStatus.FAILED, "Internal Server Error: " + e.getMessage(), null));
        }
    }
}