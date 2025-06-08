package com.etherea.services;

import com.etherea.dtos.CommandItemDTO;
import com.etherea.dtos.CommandRequestDTO;
import com.etherea.dtos.CommandResponseDTO;
import com.etherea.enums.CartStatus;
import com.etherea.enums.CommandStatus;
import com.etherea.exception.*;
import com.etherea.models.*;
import com.etherea.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommandService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CommandItemRepository commandItemRepository;

    // Retrieves all commands
    /**
     * Retrieves a paginated list of all commands.
     *
     * @param page the page number to retrieve
     * @param size the number of items per page
     * @return a {@link Page} of {@link CommandResponseDTO} containing the paginated commands
     */
    public Page<CommandResponseDTO> getAllCommands(int page, int size) {
        // Retrieve an order page
        Page<Command> commandsPage = commandRepository.findAll(PageRequest.of(page, size));

        // Convert Page<Command> to Page<CommandResponseDTO>
        return commandsPage.map(CommandResponseDTO::fromEntity);
    }

    // Retrieves all commands associated with a given user
        // Convert Page<Command> to Page<CommandResponseDTO>
        return commandsPage.map(CommandResponseDTO::fromEntity);
    }

    /**
     * Retrieves all commands associated with a specific user.
     *
     * @param userId the ID of the user whose commands are to be retrieved
     * @return a list of {@link CommandResponseDTO} representing the user's commands
     */
    public List<CommandResponseDTO> getCommandsByUserId(Long userId) {
        return commandRepository.findByUserId(userId)  // Search orders by user ID
                .stream()
                .map(CommandResponseDTO::fromEntity)   // Convert each entity into a DTO
                .collect(Collectors.toList());         // Collect results in a list
    }

    /**
     * Retrieves a specific command belonging to a specific user.
     *
     * @param userId the ID of the user
     * @param commandId the ID of the command
     * @return an {@link Optional} containing the {@link CommandResponseDTO} if found, otherwise empty
     */    public Optional<CommandResponseDTO> getCommandByUserIdAndCommandId(Long userId, Long commandId) {
        return commandRepository.findByIdAndUserId(commandId, userId)  // Search by order ID and user ID
                .map(CommandResponseDTO::fromEntity);                  // Convert entity to DTO if found
    }

    /**
     * Retrieves all items associated with a specific command.
     *
     * @param commandId the ID of the command
     * @return a list of {@link CommandItemDTO} representing the items in the command
     */
    public List<CommandItemDTO> getCommandItems(Long commandId) {
        List<CommandItem> commandItems = commandItemRepository.findByCommandId(commandId);
        return commandItems.stream()
                .map(CommandItemDTO::fromCommandItem)
                .collect(Collectors.toList());
    }

    /**
     * Creates a command using the provided CommandRequestDTO.
     *
     * @param commandRequestDTO The DTO containing all necessary data for the command.
     * @return The created Command.
     */
    @Transactional
    public Command createCommand(CommandRequestDTO commandRequestDTO) {
        if (commandRequestDTO == null) {
            throw new CommandNotFoundException("CommandRequestDTO cannot be null.");
        }

        // Check if an order already exists for this shopping cart
        if (commandRepository.existsByCartId(commandRequestDTO.getCartId())) {
            throw new CommandNotFoundException("An order already exists for this shopping cart");
        }

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(commandRequestDTO.getDeliveryAddressId())
                .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found for ID: " + commandRequestDTO.getDeliveryAddressId()));

        PaymentMethod paymentMethod = paymentRepository.findById(commandRequestDTO.getPaymentMethodId())
                .orElseThrow(() -> new PaymentMethodNotFoundException("Payment method not found for ID: " + commandRequestDTO.getPaymentMethodId()));

        Cart cart = cartRepository.findById(commandRequestDTO.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found for ID: " + commandRequestDTO.getCartId()));

        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(commandRequestDTO.getDeliveryMethodId())
                .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery method not found for ID: " + commandRequestDTO.getDeliveryMethodId()));

        if (cart.getItems().isEmpty()) {
            throw new CartNotFoundException("Cannot create order for an empty cart.");
        }

        if (cart.getStatus() == CartStatus.ORDERED) {
            throw new CommandNotFoundException("The cart has already been used for an order.");
        }

        Command command = new Command();
        command.setUser(cart.getUser());
        command.setCart(cart);
        command.setCommandDate(commandRequestDTO.getCommandDate() != null ? commandRequestDTO.getCommandDate() : LocalDateTime.now());
        command.setReferenceCode(commandRequestDTO.getReferenceCode() != null ?
                commandRequestDTO.getReferenceCode() :
                "CMD-" + System.currentTimeMillis());
        command.setStatus(commandRequestDTO.getStatus());
        command.setDeliveryAddress(deliveryAddress);
        command.setPaymentMethod(paymentMethod);
        command.setDeliveryMethod(deliveryMethod);

        // Create order items
        List<CommandItem> commandItems = createCommandItems(cart, command);
        command.setCommandItems(commandItems);

        command.setTotal(cart.calculateFinalTotal());

        commandRepository.save(command);

        cart.setStatus(CartStatus.ORDERED);
        cartRepository.save(cart);

        return command;
    }
    private List<CommandItem> createCommandItems(Cart cart, Command command) {
        List<CommandItem> commandItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            double unitPrice = cartItem.getSubTotal().doubleValue() / cartItem.getQuantity(); // Calculate unit price

            CommandItem commandItem = new CommandItem(

                    cartItem.getQuantity(),
                    unitPrice,
                    cartItem.getProduct(),
                    command, // Associate with the command
                    cartItem.getProduct().getName(),
                    cartItem.getProduct().getImage()
                    );

            commandItem.setTotalPrice(commandItem.getQuantity() * unitPrice);
            commandItems.add(commandItem);
        }
        return commandItems;
    }

    /**
     * Updates the status of a command.
     *
     * @param commandId The ID of the command to update.
     * @param newStatus The new status to set.
     */
    public void updateCommandStatus(Long commandId, CommandStatus newStatus) {
        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new CommandNotFoundException("Order not found with ID: " + commandId));

        // Checking status transitions
        if (command.getStatus() == CommandStatus.DELIVERED || command.getStatus() == CommandStatus.CANCELLED) {
            throw new CommandNotFoundException("Impossible de modifier une commande déjà livrée ou annulée.");
        }

        command.setStatus(newStatus);
        commandRepository.save(command);

        // Send an e-mail if necessary
        if (newStatus == CommandStatus.PAID) {
            String subject = "Confirmation de votre commande";
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
        emailContent.append("<p><strong>Total :</strong> ").append(command.getTotal()).append(" €</p>");

        emailContent.append("<h2>Résumé de la commande :</h2>");
        emailContent.append("<ul>");

        for (CommandItem item : command.getCommandItems()) {
            emailContent.append("<li>")
                    .append(item.getProductName())
                    .append(" - Quantité : ").append(item.getQuantity());
        }
        emailContent.append("</ul>");

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
                cart.setStatus(CartStatus.ACTIVE);
                cartRepository.save(cart);
            }

            commandRepository.save(command);
            return true;
        }
        return false;
    }
}