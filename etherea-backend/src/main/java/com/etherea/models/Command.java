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
    private String referenceCode;
    @Enumerated(EnumType.STRING)
    private CommandStatus status;
    @OneToOne( fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
    @ManyToOne( fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethod deliveryMethod;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", unique = true, nullable = false)
    private Cart cart;
    @OneToMany(mappedBy = "command", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandItem> commandItems = new ArrayList<>();
    private BigDecimal total;
    public Command() {}
    public Command(LocalDateTime commandDate, String referenceCode, CommandStatus status, DeliveryAddress deliveryAddress, DeliveryMethod deliveryMethod, Cart cart) {
        this.commandDate = commandDate;
        this.referenceCode = referenceCode;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.deliveryMethod = deliveryMethod;
        this.cart = cart;
        this.total = cart.calculateFinalTotal(); // Initialize total from shopping cart
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
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
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
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
