package com.etherea.services;

import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public PaymentResponse processPayment(PaymentRequestDTO paymentRequestDTO) {
        try {
            if (paymentRequestDTO.getPaymentMethodId() == null || paymentRequestDTO.getAmount() <= 0) {
                return new PaymentResponse("failed", null, "Données de paiement invalides.");
            }

            // Création de PaymentIntent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequestDTO.getAmount())
                    .setCurrency(paymentRequestDTO.getCurrency())
                    .setPaymentMethod(paymentRequestDTO.getPaymentMethodId())
                    .setConfirm(true)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return new PaymentResponse("success", intent.getId(), "Paiement réussi.");
        } catch (StripeException e) {
            return new PaymentResponse("failed", null, "Erreur Stripe : " + e.getMessage());
        }
    }
}
