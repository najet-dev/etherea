package com.etherea.services;

import com.etherea.dtos.CommandRequestDTO;
import com.etherea.exception.CartNotFoundException;
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
}
