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
    private StripeService stripeService;
    @Autowired
    private CartService cartService;
    @Transactional
    public PaymentResponseDTO createPaymentIntent(PaymentRequestDTO paymentRequestDTO, Long userId) throws StripeException {
        Long cartId = (paymentRequestDTO.getCartId() != null)
                ? paymentRequestDTO.getCartId()
                : cartService.getCartIdByUserId(userId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found"));

        // Calculate total order amount
        BigDecimal totalAmount = cart.calculateFinalTotal();

        // Create a PaymentIntent in Stripe
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(totalAmount);

        // Create and save a new PaymentMethod object
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setTransactionId(paymentIntent.getId());
        paymentMethod.setPaymentOption(paymentRequestDTO.getPaymentOption());
        paymentMethod.setPaymentStatus(PaymentStatus.PENDING);
        paymentMethod.setCartId(paymentRequestDTO.getCartId());
        paymentRepository.save(paymentMethod);

        // Create a DTO response containing the `transactionId` and the `clientSecret`.
        return new PaymentResponseDTO(
                PaymentStatus.PENDING,
                paymentIntent.getId(),
                paymentIntent.getClientSecret()
        );
    }
    @Transactional
    public PaymentResponseDTO confirmPayment(String paymentIntentId, String paymentMethodId) throws StripeException {

        PaymentIntent paymentIntent = stripeService.attachPaymentMethod(paymentIntentId, paymentMethodId);
        paymentIntent = stripeService.confirmPaymentIntent(paymentIntent.getId());

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
            processSuccessfulPayment(paymentMethod);
        }

        return new PaymentResponseDTO(paymentStatus, paymentIntent.getId(),paymentIntent.getClientSecret());
    }
    private void processSuccessfulPayment(PaymentMethod paymentMethod) {
        Cart cart = cartRepository.findById(paymentMethod.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Shopping cart not found"));

        if (cart.isUsed()) {
            throw new IllegalStateException("The shopping cart has already been used for an order");
        }

        User user = cart.getUser();
        DeliveryAddress deliveryAddress = user.getDefaultAddress();
        if (deliveryAddress == null) {
            throw new IllegalStateException("Default delivery address not found by user");
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

        cart.getItems().clear();
        cart.setUsed(true);
        cartRepository.save(cart);
    }
}
