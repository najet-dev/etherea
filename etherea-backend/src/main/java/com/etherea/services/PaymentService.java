package com.etherea.services;

import com.etherea.dtos.CartDTO;
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

        //Trouver l'ID utilisateur à partir du cartId
        Long userId = cartRepository.findUserIdByCartId(paymentMethod.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Aucun utilisateur trouvé pour le panier ID : " + paymentMethod.getCartId()));

        //Récupérer le panier actif de cet utilisateur
        Cart cart = cartRepository.findFirstByUserIdAndStatusOrderByIdDesc(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new CartNotFoundException("Aucun panier actif trouvé pour l'utilisateur ID : " + userId));

        // Vérifier si le panier est déjà commandé
        if (cart.getStatus() == CartStatus.ORDERED) {
            throw new IllegalStateException("Le panier a déjà été utilisé pour une commande.");
        }

        //Récupérer l'utilisateur et son adresse de livraison
        User user = cart.getUser();
        DeliveryAddress deliveryAddress = user.getDefaultAddress();
        if (deliveryAddress == null) {
            throw new IllegalStateException("Aucune adresse de livraison par défaut trouvée pour cet utilisateur.");
        }

        //Création de la commande
        CommandRequestDTO commandRequestDTO = new CommandRequestDTO();
        commandRequestDTO.setCommandDate(LocalDateTime.now());
        commandRequestDTO.setReferenceCode("CMD" + System.currentTimeMillis());
        commandRequestDTO.setStatus(CommandStatus.PENDING);
        commandRequestDTO.setCartId(cart.getId());
        commandRequestDTO.setTotal(cart.calculateFinalTotal());
        commandRequestDTO.setDeliveryAddressId(deliveryAddress.getId());
        commandRequestDTO.setPaymentMethodId(paymentMethod.getId());

        Command createdCommand = commandService.createCommand(commandRequestDTO);
        commandService.updateCommandStatus(createdCommand.getId(), CommandStatus.PAID);

        //Mettre à jour le panier après le paiement
        cart.getItems().clear();
        cart.setStatus(CartStatus.ORDERED);
        cartRepository.save(cart);
    }

}