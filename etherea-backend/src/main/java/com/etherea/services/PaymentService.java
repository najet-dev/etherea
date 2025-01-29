package com.etherea.services;

import com.etherea.dtos.CartDTO;
import com.etherea.dtos.CommandRequestDTO;
import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponseDTO;
import com.etherea.enums.CommandStatus;
import com.etherea.enums.PaymentStatus;
import com.etherea.exception.CartNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.CartRepository;
import com.etherea.repositories.CommandRepository;
import com.etherea.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private CommandService commandService;
    @Autowired
    private CartItemService cartItemService;
    @Transactional
    public PaymentResponseDTO createPaymentIntent(PaymentRequestDTO paymentRequestDTO) throws StripeException {
        Cart cart = cartRepository.findById(paymentRequestDTO.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        BigDecimal totalAmount = cart.calculateFinalTotal();
        long amountInCents = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("eur")
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);

        return new PaymentResponseDTO(paymentIntent.getClientSecret());
    }
}



