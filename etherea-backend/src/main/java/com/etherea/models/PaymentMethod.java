package com.etherea.models;

import com.etherea.enums.PaymentOption;
import jakarta.persistence.*;

@Entity
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentOption paymentOption;
    private String cardNumber;
    private String expirationDate;
    private String cardHolderName;
    private String securityCode;

    // Attributs supplémentaires pour le suivi des paiements
    private String transactionId;      // ID de transaction
    private String paymentStatus;      // Statut du paiement
    public PaymentMethod() {}

    public PaymentMethod(PaymentOption paymentOption, String cardNumber, String expirationDate,
                         String cardHolderName, String securityCode, String transactionId,
                         String paymentStatus) {
        this.paymentOption = paymentOption;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardHolderName = cardHolderName;
        this.securityCode = securityCode;
        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
    }
    // Getters et Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public PaymentOption getPaymentOption() {
        return paymentOption;
    }
    public void setPaymentOption(PaymentOption paymentOption) {
        this.paymentOption = paymentOption;
    }
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public String getCardHolderName() {
        return cardHolderName;
    }
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    public String getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
