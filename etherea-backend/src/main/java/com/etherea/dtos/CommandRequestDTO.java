package com.etherea.dtos;

import com.etherea.enums.CommandStatus;
import com.etherea.models.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommandRequestDTO {
    private LocalDateTime commandDate = LocalDateTime.now();
    private String referenceCode;
    private CommandStatus status = CommandStatus.PENDING;

    // IDs required for creation (associations)
    @NotNull @Positive
    private Long deliveryAddressId; // used only for snapshot
    @NotNull @Positive
    private Long paymentMethodId;
    @NotNull @Positive
    private Long deliveryMethodId;
    @NotNull @Positive
    private Long cartId;
    @NotNull @Positive
    private Long userId;
    private BigDecimal total = BigDecimal.ZERO;
    public CommandRequestDTO() {}
    public CommandRequestDTO(String referenceCode, Long deliveryAddressId, Long paymentMethodId,
                             Long deliveryMethodId, Long cartId, Long userId) {
        this.referenceCode = referenceCode;
        this.deliveryAddressId = deliveryAddressId;
        this.paymentMethodId = paymentMethodId;
        this.deliveryMethodId = deliveryMethodId;
        this.cartId = cartId;
        this.userId = userId;
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
        this.deliveryAddressId = deliveryAddressId;
    }
    public Long getPaymentMethodId() {
        return paymentMethodId;
    }
    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    public Long getDeliveryMethodId() {
        return deliveryMethodId;
    }
    public void setDeliveryMethodId(Long deliveryMethodId) {
        this.deliveryMethodId = deliveryMethodId;
    }
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public Command toEntity(DeliveryAddress deliveryAddress,
                            PaymentMethod paymentMethod,
                            DeliveryMethod deliveryMethod,
                            User user,
                            Cart cart) {

        Command command = new Command();
        command.setCommandDate(this.commandDate);
        command.setReferenceCode(this.referenceCode);
        command.setStatus(this.status);
        command.setPaymentMethod(paymentMethod);
        command.setDeliveryMethod(deliveryMethod);
        command.setUser(user);
        command.setCart(cart);
        command.setTotal(this.total);

        // Snapshot of delivery address
        if (deliveryAddress != null) {
            command.setAddress(deliveryAddress.getAddress());
            command.setZipCode(deliveryAddress.getZipCode());
            command.setCity(deliveryAddress.getCity());
            command.setCountry(deliveryAddress.getCountry());
            command.setPhoneNumber(deliveryAddress.getPhoneNumber());
        }
        return command;
    }
}
