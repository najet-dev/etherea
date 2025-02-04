package com.etherea.services;

import com.etherea.dtos.CommandRequestDTO;
import com.etherea.enums.CommandStatus;
import com.etherea.exception.CartNotFoundException;
import com.etherea.exception.CommandNotFoundException;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommandService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EmailService emailService;


    /**
     * Creates a command using the provided CommandRequestDTO.
     *
     * @param commandRequestDTO The DTO containing all necessary data for the command.
     * @return The created Command.
     */
    @Transactional
    public Command createCommand(CommandRequestDTO commandRequestDTO) {
        if (commandRequestDTO == null) {
            throw new IllegalArgumentException("CommandRequestDTO cannot be null.");
        }

        // Validate Delivery Address
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(commandRequestDTO.getDeliveryAddressId())
                .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found for ID: " + commandRequestDTO.getDeliveryAddressId()));

        // Validate Payment Method
        PaymentMethod paymentMethod = paymentRepository.findById(commandRequestDTO.getPaymentMethodId())
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found for ID: " + commandRequestDTO.getPaymentMethodId()));

        // Validate Cart
        Cart cart = cartRepository.findById(commandRequestDTO.getCart().getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found for ID: " + commandRequestDTO.getCart().getCartId()));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order for an empty cart.");
        }

        if (cart.isUsed()) {
            throw new IllegalStateException("The cart has already been used for an order.");
        }

        // Create Command
        Command command = new Command();
        command.setUser(cart.getUser());
        command.setCart(cart);
        command.setCommandDate(commandRequestDTO.getCommandDate() != null ? commandRequestDTO.getCommandDate() : LocalDateTime.now());
        command.setReferenceCode(commandRequestDTO.getReferenceCode());
        command.setStatus(commandRequestDTO.getStatus());
        command.setDeliveryAddress(deliveryAddress);
        command.setPaymentMethod(paymentMethod);
        command.setTotal(cart.calculateFinalTotal());

        // Save Command
        commandRepository.save(command);

        // Mark Cart as Used
        cart.setUsed(true);
        cartRepository.save(cart);

        return command;
    }
    /**
     * Updates the status of a command.
     *
     * @param commandId The ID of the command to update.
     * @param newStatus The new status to set.
     */
    @Transactional
    public void updateCommandStatus(Long commandId, CommandStatus newStatus) {
        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new CommandNotFoundException("Order not found with ID: " + commandId));

        command.setStatus(newStatus);
        commandRepository.save(command);

        // Envoyer l'e-mail si la commande est expédiée
        if (newStatus == CommandStatus.PAID) {
            String subject = "Confirmation d'expédition de votre commande";
            String emailContent = generateOrderConfirmationEmail(command);
            emailService.sendOrderConfirmation(command.getUser().getUsername(), subject, emailContent);
        }
    }
    @Transactional
    private String generateOrderConfirmationEmail(Command command) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("<h1>Votre commande a été expédiée !</h1>");
        emailContent.append("<p>Merci pour votre achat. Voici les détails de votre commande :</p>");
        emailContent.append("<p><strong>Numéro de commande :</strong> ").append(command.getReferenceCode()).append("</p>");

        DeliveryAddress address = command.getDeliveryAddress();
        emailContent.append("<p><strong>Adresse de livraison :</strong> ")
                .append(address.getAddress()).append(", ")
                .append(address.getCity()).append(", ")
                .append(address.getZipCode()).append(", ")
                .append(address.getCountry()).append("</p>");

        emailContent.append("<h2>Articles commandés :</h2>");
        emailContent.append("<ul>");

        for (CommandItem item : command.getCommandItems()) {
            emailContent.append("<li>")
                    .append(item.getProductName())
                    .append(" - Quantité : ").append(item.getQuantity())
                    .append(" - Prix : ").append(item.getUnitPrice()).append(" €</li>");
        }
        emailContent.append("</ul>");

        emailContent.append("<p><strong>Total :</strong> ").append(command.getTotal()).append(" €</p>");
        emailContent.append("<p>Nous espérons vous revoir bientôt !</p>");

        return emailContent.toString();
    }


    @Transactional
    public boolean cancelCommand(Long commandId) {
        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new CommandNotFoundException("Order not found"));

        if (command.getStatus() == CommandStatus.PENDING) {
            command.setStatus(CommandStatus.CANCELLED);

            // Reset shopping cart
            Cart cart = command.getCart();
            if (cart != null) {
                cart.setUsed(false);
                cartRepository.save(cart);
            }

            commandRepository.save(command);
            return true;
        }
        return false;
    }
}
