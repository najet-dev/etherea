package com.etherea.dtos;

import com.etherea.enums.CommandStatus;
import com.etherea.models.Command;
import com.etherea.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class CommandResponseDTO {

    private Long id;
    private LocalDateTime commandDate;
    private String referenceCode;
    private CommandStatus status;
    private BigDecimal total;
    private String firstName;
    private String lastName;
    private String deliveryAddress;
    private String paymentMethod;
    private String deliveryMethod;
    public CommandResponseDTO() {
    }
    public CommandResponseDTO(Long id, LocalDateTime commandDate, String referenceCode, CommandStatus status,
                              BigDecimal total, String firstName, String lastName, String deliveryAddress,
                              String paymentMethod, String deliveryMethod) {
        this.id = id;
        this.commandDate = commandDate;
        this.referenceCode = referenceCode;
        this.status = status;
        this.total = total;
        this.firstName = firstName;
        this.lastName = lastName;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.deliveryMethod = deliveryMethod;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getCommandDate() {
        return commandDate;
    }
    public void setCommandDate(LocalDateTime commandDate) {
        this.commandDate = commandDate;
    }
    public String getReferenceCode() {
        return referenceCode;
    }
    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }
    public CommandStatus getStatus() {
        return status;
    }
    public void setStatus(CommandStatus status) {
        this.status = status;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    // Mapper pour transformer une entité en DTO
    public static CommandResponseDTO fromEntity(Command command) {
        return new CommandResponseDTO(
                command.getId(),
                command.getCommandDate(),
                command.getReferenceCode(),
                command.getStatus(),
                command.getTotal(),
                Optional.ofNullable(command.getUser())
                        .map(User::getFirstName)
                        .orElse("Utilisateur inconnu"),
                Optional.ofNullable(command.getUser())
                        .map(User::getLastName)
                        .orElse("Utilisateur inconnu"),
                Optional.ofNullable(command.getDeliveryAddress())
                        .map(address -> address.getAddress() + ", " + address.getCity())
                        .orElse("Adresse non définie"),
                Optional.ofNullable(command.getPaymentMethod())
                        .map(payment -> Optional.ofNullable(payment.getPaymentOption())
                                .map(Enum::toString)
                                .orElse("Option de paiement non définie"))
                        .orElse("Méthode de paiement non définie"),
                Optional.ofNullable(command.getDeliveryMethod())
                        .map(delivery -> delivery.getDeliveryType().getDeliveryName().toString())  // Conversion de DeliveryName en String
                        .orElse("Méthode de livraison non définie")
        );
    }

}