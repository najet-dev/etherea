package com.etherea.services;

import com.etherea.dtos.CartDTO;
import com.etherea.dtos.CommandRequestDTO;
import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponseDTO;
import com.etherea.enums.CommandStatus;
import com.etherea.enums.PaymentStatus;
import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.CommandNotFoundException;
import com.etherea.models.Cart;
import com.etherea.models.Command;
import com.etherea.models.PaymentMethod;
import com.etherea.repositories.CartRepository;
import com.etherea.repositories.CommandRepository;
import com.etherea.repositories.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
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

    @Transactional
    public PaymentResponseDTO createPaymentIntent(PaymentRequestDTO paymentRequestDTO) throws StripeException {
        // Valider le panier
        Cart cart = cartRepository.findById(paymentRequestDTO.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        BigDecimal totalAmount = cart.calculateFinalTotal();

        // Créer une intention de paiement Stripe
        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount.multiply(BigDecimal.valueOf(100)).longValue()) // Montant en centimes
                .setCurrency("eur")
                .addPaymentMethodType("card")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(createParams);

        // Enregistrer l'intention de paiement
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setTransactionId(paymentIntent.getId());
        paymentMethod.setPaymentOption(paymentRequestDTO.getPaymentOption());
        paymentMethod.setPaymentStatus(PaymentStatus.PENDING); // Toujours PENDING à la création
        paymentMethod.setCartId(paymentRequestDTO.getCartId());
        paymentRepository.save(paymentMethod);

        logger.info("PaymentIntent created with ID: {}", paymentIntent.getId());

        return new PaymentResponseDTO(PaymentStatus.PENDING, paymentIntent.getId());
    }

    @Transactional
    public PaymentResponseDTO confirmPayment(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            logger.error("Failed to retrieve PaymentIntent with ID: {}", paymentIntentId, e);
            throw new IllegalStateException("Unable to retrieve PaymentIntent from Stripe", e);
        }

        // Vérifier le statut du paiement
        PaymentStatus paymentStatus = "succeeded".equals(paymentIntent.getStatus())
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;

        // Récupérer et valider la méthode de paiement
        PaymentMethod paymentMethod = paymentRepository.findByTransactionId(paymentIntentId);
        if (paymentMethod == null) {
            logger.error("PaymentMethod not found for PaymentIntent ID: {}", paymentIntentId);
            throw new IllegalArgumentException("Payment method not found for transaction ID: " + paymentIntentId);
        }

        // Mettre à jour le statut de la méthode de paiement
        paymentMethod.setPaymentStatus(paymentStatus);
        paymentRepository.save(paymentMethod);

        logger.info("Payment status updated to {} for PaymentIntent ID: {}", paymentStatus, paymentIntentId);

        // Créer une commande si le paiement est réussi
        if (paymentStatus == PaymentStatus.SUCCESS) {
            Cart cart = cartRepository.findById(paymentMethod.getCartId())
                    .orElseThrow(() -> new CartNotFoundException("Cart not found"));

            CommandRequestDTO commandRequestDTO = new CommandRequestDTO(
                    LocalDateTime.now(),
                    "CMD" + System.currentTimeMillis(),
                    CommandStatus.PAID,
                    cart.getUser().getDefaultAddress().getId(),
                    paymentMethod.getId(),
                    CartDTO.fromCart(cart, null)
            );
            Command createdCommand = commandService.createCommand(commandRequestDTO);

            // Marquer le panier comme utilisé et vider les articles
            cart.setUsed(true);
            cart.getItems().clear();
            cartRepository.save(cart);

            logger.info("Command created for user ID: {} with reference code: {}",
                    cart.getUser().getId(), createdCommand.getReferenceCode());
        }

        return new PaymentResponseDTO(paymentStatus, paymentIntentId);
    }
}
