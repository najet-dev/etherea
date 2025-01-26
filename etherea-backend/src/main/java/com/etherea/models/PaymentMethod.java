package com.etherea.models;

import com.etherea.enums.PaymentOption;
import com.etherea.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentOption paymentOption;
    private String transactionId;      // ID de transaction
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Column(nullable = false)
    private Long cartId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    public PaymentMethod() {}
    public PaymentMethod(PaymentOption paymentOption, String transactionId, PaymentStatus paymentStatus) {
        this.paymentOption = paymentOption;
        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
    }
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
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
}
