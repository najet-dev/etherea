package com.etherea.services;

import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponseDTO;
import com.etherea.enums.PaymentOption;
import com.etherea.enums.PaymentStatus;
import com.etherea.exception.CartNotFoundException;
import com.etherea.models.Cart;
import com.etherea.models.PaymentMethod;
import com.etherea.repositories.CartRepository;
import com.etherea.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CartItemService cartItemService;

    /**
     * Processes a payment and associates the cart with an order upon success.
     *
     * @param paymentRequestDTO The payment request details.
     * @return PaymentResponseDTO with transaction details.
     * @throws StripeException In case of Stripe payment processing errors.
     */
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO) throws StripeException {
        // Retrieve the user's cart
        Cart cart = cartRepository.findById(paymentRequestDTO.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        BigDecimal totalAmount = cart.calculateFinalTotal();

        // Create a Stripe payment request
        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount.multiply(BigDecimal.valueOf(100)).longValue()) // Amount in cents
                .setCurrency("eur")
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);

        // Confirm payment with a test payment method
        PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa") // Stripe test payment method (e.g. Visa card)
                .build();

        PaymentIntent confirmedPaymentIntent = paymentIntent.confirm(confirmParams);

        // Check transaction status after confirmation
        PaymentStatus paymentStatus;
        if ("succeeded".equals(confirmedPaymentIntent.getStatus())) {
            paymentStatus = PaymentStatus.SUCCESS;
        } else {
            paymentStatus = PaymentStatus.FAILED;
        }

        // Save transaction details
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setTransactionId(confirmedPaymentIntent.getId());
        paymentMethod.setPaymentOption(paymentRequestDTO.getCardNumber().startsWith("4") ? PaymentOption.CREDIT_CARD : PaymentOption.PAYPAL);
        paymentMethod.setPaymentStatus(paymentStatus);

        // Save the PaymentMethod to obtain its ID
        paymentRepository.save(paymentMethod);

        // Récupérer l'ID du mode de paiement
        Long paymentMethodId = paymentMethod.getId();

        // Validate cart and create order if payment succeeded
        if (paymentStatus == PaymentStatus.SUCCESS) {
            cartItemService.validateCartAndCreateOrder(cart.getUser().getId(), paymentMethodId);
            return new PaymentResponseDTO("Payment successful", confirmedPaymentIntent.getId());
        } else {
            return new PaymentResponseDTO("Payment failed", confirmedPaymentIntent.getId());
        }
    }
}
