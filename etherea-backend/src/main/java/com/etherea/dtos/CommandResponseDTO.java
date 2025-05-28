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

    // Separate address fields (snapshot)
    private String address;
    private int zipCode;
    private String city;
    private String country;
    private String phoneNumber;
    private String paymentMethod;
    private String deliveryMethod;
    public CommandResponseDTO() {}
    public CommandResponseDTO(Long id, LocalDateTime commandDate, String referenceCode, CommandStatus status,
                              BigDecimal total, String firstName, String lastName,
                              String address, int zipCode, String city, String country, String phoneNumber,
                              String paymentMethod, String deliveryMethod) {
        this.id = id;
        this.commandDate = commandDate;
        this.referenceCode = referenceCode;
        this.status = status;
        this.total = total;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.paymentMethod = paymentMethod;
        this.deliveryMethod = deliveryMethod;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getCommandDate() { return commandDate; }
    public void setCommandDate(LocalDateTime commandDate) { this.commandDate = commandDate; }
    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
    public CommandStatus getStatus() { return status; }
    public void setStatus(CommandStatus status) { this.status = status; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getZipCode() { return zipCode; }
    public void setZipCode(int zipCode) { this.zipCode = zipCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    // Mapper : Entity -> DTO
    public static CommandResponseDTO fromEntity(Command command) {
        String firstName = Optional.ofNullable(command.getUser())
                .map(User::getFirstName)
                .orElse("Inconnu");

        String lastName = Optional.ofNullable(command.getUser())
                .map(User::getLastName)
                .orElse("Inconnu");

        String payment = Optional.ofNullable(command.getPaymentMethod())
                .map(pm -> pm.getPaymentOption() != null ? pm.getPaymentOption().toString() : "Non spécifiée")
                .orElse("Non spécifiée");

        String delivery = Optional.ofNullable(command.getDeliveryMethod())
                .map(dm -> dm.getDeliveryType() != null ? dm.getDeliveryType().getDeliveryName().toString() : "Non spécifiée")
                .orElse("Non spécifiée");

        return new CommandResponseDTO(
                command.getId(),
                command.getCommandDate(),
                command.getReferenceCode(),
                command.getStatus(),
                command.getTotal(),
                firstName,
                lastName,
                command.getAddress(),
                command.getZipCode(),
                command.getCity(),
                command.getCountry(),
                command.getPhoneNumber(),
                payment,
                delivery
        );
    }
}
