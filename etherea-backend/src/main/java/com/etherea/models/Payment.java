package com.etherea.models;

import com.etherea.enums.PaymentStatus;
import jakarta.persistence.*;

import java.util.Date;
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private Date paymentDate;
    private PaymentStatus status;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "commandId", referencedColumnName = "id")
    private Command command;
    public Payment() {
    }
    public Payment(double amount, Date paymentDate, PaymentStatus status, Command command) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.command = command;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Date getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    public PaymentStatus getStatus() {
        return status;
    }
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    public Command getCommand() {
        return command;
    }
    public void setCommand(Command command) {
        this.command = command;
    }
}

