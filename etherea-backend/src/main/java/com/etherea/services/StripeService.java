package com.etherea.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class StripeService {
    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);
    public PaymentIntent createPaymentIntent(BigDecimal totalAmount) throws StripeException {
        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount.multiply(BigDecimal.valueOf(100))
                        .setScale(0, RoundingMode.HALF_UP)
                        .longValue()) // Amount in centimes

                .setCurrency("eur")
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);
        logger.info("PaymentIntent created with ID: {}", paymentIntent.getId());

        return paymentIntent;
    }
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
    public PaymentIntent attachPaymentMethod(String paymentIntentId, String paymentMethodId) throws StripeException {
        PaymentIntent paymentIntent = retrievePaymentIntent(paymentIntentId);
        if ("requires_payment_method".equals(paymentIntent.getStatus())) {
            paymentIntent = paymentIntent.update(PaymentIntentUpdateParams.builder()
                    .setPaymentMethod(paymentMethodId)
                    .build());
            logger.info("Payment method attached to PaymentIntent {}", paymentIntentId);
        }
        return paymentIntent;
    }
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = retrievePaymentIntent(paymentIntentId);
        return paymentIntent.confirm();
    }
}
