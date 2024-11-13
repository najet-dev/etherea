package com.etherea.models;

import com.etherea.enums.CommandStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime commandDate;

    @Enumerated(EnumType.STRING)
    private CommandStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "command", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandItem> commandItems = new ArrayList<>();

    public Command() {}

    public Command(LocalDateTime commandDate, CommandStatus status, DeliveryAddress deliveryAddress) {
        this.commandDate = commandDate;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters et Setters
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
    public CommandStatus getStatus() {
        return status;
    }
    public void setStatus(CommandStatus status) {
        this.status = status;
    }
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<CommandItem> getCommandItems() {
        return commandItems;
    }
    public void setCommandItems(List<CommandItem> commandItems) {
        this.commandItems = commandItems;
    }
}
