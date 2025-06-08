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

    /**
     * Creates a Stripe PaymentIntent for the specified total amount.
     *
     * @param totalAmount the total payment amount in euros
     * @return the created {@link PaymentIntent}
     * @throws StripeException if an error occurs during the creation process
     */
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

    /**
     * Retrieves an existing PaymentIntent by its ID.
     *
     * @param paymentIntentId the ID of the PaymentIntent to retrieve
     * @return the {@link PaymentIntent} object
     * @throws StripeException if retrieval fails or the ID is invalid
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Attaches a payment method to a PaymentIntent if it requires one.
     *
     * @param paymentIntentId  the ID of the PaymentIntent
     * @param paymentMethodId  the ID of the payment method to attach
     * @return the updated {@link PaymentIntent}
     * @throws StripeException if updating the intent fails
     */public PaymentIntent attachPaymentMethod(String paymentIntentId, String paymentMethodId) throws StripeException {
        PaymentIntent paymentIntent = retrievePaymentIntent(paymentIntentId);
        if ("requires_payment_method".equals(paymentIntent.getStatus())) {
            paymentIntent = paymentIntent.update(PaymentIntentUpdateParams.builder()
                    .setPaymentMethod(paymentMethodId)
                    .build());
            logger.info("Payment method attached to PaymentIntent {}", paymentIntentId);
        }
        return paymentIntent;
    }

    /**
     * Confirms a PaymentIntent to finalize the payment process.
     *
     * @param paymentIntentId the ID of the PaymentIntent to confirm
     * @return the confirmed {@link PaymentIntent}
     * @throws StripeException if confirmation fails
     */public PaymentIntent confirmPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = retrievePaymentIntent(paymentIntentId);
        return paymentIntent.confirm();
    }
}
