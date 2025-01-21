package com.etherea.dtos;

import com.etherea.enums.CommandStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommandRequestDTO {
    private LocalDateTime commandDate;
    private String referenceCode;
    private CommandStatus status = CommandStatus.PENDING;
    private Long deliveryAddressId;
    private Long paymentMethodId;
    private CartDTO cart;
    private BigDecimal total = BigDecimal.ZERO;

    public CommandRequestDTO() {}

    public CommandRequestDTO(LocalDateTime commandDate, String referenceCode, CommandStatus status,
                             Long deliveryAddressId, Long paymentMethodId, CartDTO cart) {
        this.commandDate = commandDate != null ? commandDate : LocalDateTime.now();
        this.referenceCode = referenceCode;
        this.status = status != null ? status : CommandStatus.PENDING;
        this.deliveryAddressId = deliveryAddressId;
        this.paymentMethodId = paymentMethodId;
        this.cart = cart;
        this.total = cart != null ? cart.getFinalTotal() : BigDecimal.ZERO;
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
    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }
    public void setDeliveryAddressId(Long deliveryAddressId) {
        if (deliveryAddressId == null || deliveryAddressId <= 0) {
            throw new IllegalArgumentException("Delivery address ID must be a positive value.");
        }
        this.deliveryAddressId = deliveryAddressId;
    }
    public Long getPaymentMethodId() {
        return paymentMethodId;
    }
    public void setPaymentMethodId(Long paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId <= 0) {
            throw new IllegalArgumentException("Payment method ID must be a positive value.");
        }
        this.paymentMethodId = paymentMethodId;
    }
    public CartDTO getCart() {
        return cart;
    }
    public void setCart(CartDTO cart) {
        this.cart = cart;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
