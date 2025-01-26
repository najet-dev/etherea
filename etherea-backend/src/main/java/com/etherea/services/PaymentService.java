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

        // Create a Stripe payment intention
        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount.multiply(BigDecimal.valueOf(100)).longValue()) // Amount in centimes
                .setCurrency("eur")
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setTransactionId(paymentIntent.getId());
        paymentMethod.setPaymentOption(paymentRequestDTO.getPaymentOption());
        paymentMethod.setPaymentStatus(PaymentStatus.PENDING);
        paymentMethod.setCartId(paymentRequestDTO.getCartId());
        paymentRepository.save(paymentMethod);

        logger.info("PaymentIntent created with ID: {}", paymentIntent.getId());

        return new PaymentResponseDTO(PaymentStatus.PENDING, paymentIntent.getId());
    }

    @Transactional
    public PaymentResponseDTO confirmPayment(String paymentIntentId, String paymentMethodId) throws StripeException {
        PaymentIntent paymentIntent;

        try {
            // Fetch from PaymentIntent
            paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if ("requires_payment_method".equals(paymentIntent.getStatus())) {
                logger.info("Attaching payment method {} to PaymentIntent {}", paymentMethodId, paymentIntentId);

                paymentIntent = paymentIntent.update(PaymentIntentUpdateParams.builder()
                        .setPaymentMethod(paymentMethodId)
                        .build());

                logger.info("Payment method attached successfully.");
            }

            // Confirm PaymentIntent
            paymentIntent = paymentIntent.confirm();
            logger.info("PaymentIntent confirmed with status: {}", paymentIntent.getStatus());
        } catch (StripeException e) {
            logger.error("Error during PaymentIntent confirmation: {}", e.getMessage());
            throw new IllegalStateException("Unable to confirm PaymentIntent", e);
        }

        PaymentStatus paymentStatus = "succeeded".equals(paymentIntent.getStatus())
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;

        PaymentMethod paymentMethod = paymentRepository.findByTransactionId(paymentIntentId);
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method not found for transaction ID: " + paymentIntentId);
        }

        paymentMethod.setPaymentStatus(paymentStatus);
        paymentRepository.save(paymentMethod);

        if (paymentStatus == PaymentStatus.SUCCESS) {
            logger.info("Payment succeeded. Creating order...");

            Cart cart = cartRepository.findById(paymentMethod.getCartId())
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for ID: " + paymentMethod.getCartId()));

            if (cart.isUsed()) {
                throw new IllegalStateException("The cart has already been used for an order.");
            }

            User user = cart.getUser();
            DeliveryAddress deliveryAddress = user.getDefaultAddress();
            if (deliveryAddress == null) {
                throw new IllegalStateException("Default delivery address not found for user ID: " + user.getId());
            }

            CommandRequestDTO commandRequestDTO = new CommandRequestDTO();
            commandRequestDTO.setCommandDate(LocalDateTime.now());
            commandRequestDTO.setReferenceCode("CMD" + System.currentTimeMillis());
            commandRequestDTO.setStatus(CommandStatus.PENDING);
            commandRequestDTO.setCart(CartDTO.fromCart(cart, null));
            commandRequestDTO.setTotal(cart.calculateFinalTotal());
            commandRequestDTO.setDeliveryAddressId(deliveryAddress.getId());
            commandRequestDTO.setPaymentMethodId(paymentMethod.getId());

            Command createdCommand = commandService.createCommand(commandRequestDTO);

            commandService.updateCommandStatus(createdCommand.getId(), CommandStatus.PAID);

            // Empty shopping cart items
            cart.getItems().clear();
            cart.setUsed(true);
            cartRepository.save(cart);

            logger.info("Order created and cart emptied for user ID: {}", user.getId());
        }

        return new PaymentResponseDTO(paymentStatus, paymentIntentId);
    }

}
