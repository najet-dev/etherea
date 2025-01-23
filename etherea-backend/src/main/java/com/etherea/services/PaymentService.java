package com.etherea.services;

import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponseDTO;
import com.etherea.enums.CommandStatus;
import com.etherea.enums.PaymentOption;
import com.etherea.enums.PaymentStatus;
import com.etherea.exception.CartNotFoundException;
import com.etherea.models.Cart;
import com.etherea.models.Command;
import com.etherea.models.PaymentMethod;
import com.etherea.repositories.CartRepository;
import com.etherea.repositories.CommandRepository;
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
    private CommandRepository commandRepository;
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

        Cart cart = cartRepository.findById(paymentRequestDTO.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        BigDecimal totalAmount = cart.calculateFinalTotal();

        // Create a Stripe payment request
        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount.multiply(BigDecimal.valueOf(100)).longValue()) // Amount in centimes
                .setCurrency("eur")
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);

        // Confirm payment with a payment method
        PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa") // Stripe test method (e.g. Visa card)
                .build();

        PaymentIntent confirmedPaymentIntent = paymentIntent.confirm(confirmParams);

        // Determine payment status after confirmation
        PaymentStatus paymentStatus = "succeeded".equals(confirmedPaymentIntent.getStatus())
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;

        // Save transaction details
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setTransactionId(confirmedPaymentIntent.getId());
        paymentMethod.setPaymentOption(paymentRequestDTO.getPaymentOption());
        paymentMethod.setPaymentStatus(paymentStatus);

        paymentRepository.save(paymentMethod);

        // Validate basket and create an order if payment is successful
        if (paymentStatus == PaymentStatus.SUCCESS) {
            Command createdCommand = cartItemService.validateCartAndCreateOrder(cart.getUser().getId(), paymentMethod.getId());

            // Update order status to “PAID
            createdCommand.setStatus(CommandStatus.PAID);
            commandRepository.save(createdCommand);

            return new PaymentResponseDTO("Successful payment", confirmedPaymentIntent.getId());
        } else {
            return new PaymentResponseDTO("Payment failure", confirmedPaymentIntent.getId());
        }
    }
}
