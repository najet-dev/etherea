package com.etherea.dtos;

import com.etherea.enums.CommandStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour gérer les requêtes de commande.
 */
public class CommandRequestDTO {
    private LocalDateTime commandDate;
    private String referenceCode;
    private CommandStatus status = CommandStatus.PENDING; // Statut par défaut
    private Long deliveryAddressId; // Identifiant de l'adresse de livraison
    private Long deliveryMethodId; // Identifiant de la méthode de livraison
    private Long paymentMethodId; // Identifiant du mode de paiement
    private CartDTO cart; // Informations sur le panier
    private BigDecimal total = BigDecimal.ZERO; // Valeur par défaut pour le total

    public CommandRequestDTO() {}

    public CommandRequestDTO(LocalDateTime commandDate, String referenceCode, CommandStatus status,
                             Long deliveryAddressId, Long deliveryMethodId, Long paymentMethodId, CartDTO cart) {
        this.commandDate = commandDate != null ? commandDate : LocalDateTime.now(); // Date par défaut
        this.referenceCode = referenceCode;
        this.status = status != null ? status : CommandStatus.PENDING; // Statut par défaut
        this.deliveryAddressId = deliveryAddressId;
        this.deliveryMethodId = deliveryMethodId;
        this.paymentMethodId = paymentMethodId;
        this.cart = cart;
        this.total = cart != null ? cart.getFinalTotal() : BigDecimal.ZERO; // Calcul du total basé sur le panier
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
    public Long getDeliveryMethodId() {
        return deliveryMethodId;
    }
    public void setDeliveryMethodId(Long deliveryMethodId) {
        this.deliveryMethodId = deliveryMethodId;
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
