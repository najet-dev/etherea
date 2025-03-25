package com.etherea.services;

import com.etherea.dtos.CommandRequestDTO;
import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponseDTO;
import com.etherea.enums.CartStatus;
import com.etherea.enums.CommandStatus;
import com.etherea.enums.PaymentStatus;
import com.etherea.exception.CartNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.CartRepository;
import com.etherea.repositories.CommandRepository;
import com.etherea.repositories.DeliveryMethodRepository;
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
import java.util.Optional;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private CommandService commandService;
    @Autowired
    private StripeService stripeService;
    @Autowired
    private CartService cartService;

    /**
     * Creates a payment intent using Stripe for a specified cart.
     *
     * @param paymentRequestDTO Payment request containing cart and payment details.
     * @param userId The ID of the user making the payment.
     * @return A PaymentResponseDTO containing the payment status, transaction ID, and client secret.
     * @throws StripeException If an error occurs with the Stripe payment service.
     */
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

        // Return a DTO with transactionId and clientSecret
        return new PaymentResponseDTO(
                PaymentStatus.PENDING,
                paymentIntent.getId(),
                paymentIntent.getClientSecret()
        );
    }

    /**
     * Confirms a payment using the provided payment intent and payment method IDs.
     *
     * @param paymentIntentId The ID of the payment intent.
     * @param paymentMethodId The ID of the payment method.
     * @return A PaymentResponseDTO containing the final payment status, transaction ID, and client secret.
     * @throws StripeException If an error occurs with the Stripe payment service.
     */
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

        return new PaymentResponseDTO(paymentStatus, paymentIntent.getId(), paymentIntent.getClientSecret());
    }

    /**
     * Handles the post-payment process for a successful payment.
     * This includes creating a new order and updating the cart status.
     *
     * @param paymentMethod The payment method associated with the successful payment.
     */
    private void processSuccessfulPayment(PaymentMethod paymentMethod) {
        // Retrieve user ID from cartId
        Long userId = cartRepository.findUserIdByCartId(paymentMethod.getCartId())
                .orElseThrow(() -> new CartNotFoundException("No user found for cart ID: " + paymentMethod.getCartId()));

        // Retrieve the active cart
        Cart cart = cartRepository.findTopByUserIdAndStatusOrderByIdDesc(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new CartNotFoundException("No active cart found for user ID: " + userId));

        if (cart.getStatus() == CartStatus.ORDERED) {
            throw new IllegalStateException("The cart has already been used for an order.");
        }

        // Retrieve the user and their default delivery address
        User user = cart.getUser();
        DeliveryAddress deliveryAddress = user.getDefaultAddress();
        if (deliveryAddress == null) {
            throw new IllegalStateException("No default delivery address found for this user.");
        }

        // Retrieve the delivery method
        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(cart.getDeliveryMethod().getId())
                .orElseThrow(() -> new IllegalStateException("No delivery method found for this cart."));

        // Create a new command (order)
        CommandRequestDTO commandRequestDTO = new CommandRequestDTO();
        commandRequestDTO.setCommandDate(LocalDateTime.now());
        commandRequestDTO.setReferenceCode("CMD" + System.currentTimeMillis());  // Ensure this generates a unique reference code
        commandRequestDTO.setStatus(CommandStatus.PENDING);
        commandRequestDTO.setCartId(cart.getId());
        commandRequestDTO.setTotal(cart.calculateFinalTotal());
        commandRequestDTO.setDeliveryAddressId(deliveryAddress.getId());
        commandRequestDTO.setPaymentMethodId(paymentMethod.getId());
        commandRequestDTO.setDeliveryMethodId(deliveryMethod.getId());

        // Check if the cart has already been used in a command
        Optional<Command> existingCommand = commandRepository.findByCartIdAndUserId(cart.getId(), userId);
        if (existingCommand.isPresent()) {
            throw new IllegalStateException("This cart has already been used for an order.");
        }

        // Create the order before changing the cart status
        Command createdCommand = commandService.createCommand(commandRequestDTO);
        commandService.updateCommandStatus(createdCommand.getId(), CommandStatus.PAID);

        // Clear the cart and mark it as ORDERED
        cart.getItems().clear();
        cart.setStatus(CartStatus.ORDERED);
        cartRepository.save(cart);
    }
}