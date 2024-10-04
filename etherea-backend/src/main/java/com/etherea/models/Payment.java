package com.etherea.models;

import com.etherea.enums.CommandStatus;
import jakarta.persistence.*;

import java.util.Date;
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private Date paymentDate;
    @Enumerated(EnumType.STRING)
    public CommandStatus CommandStatus;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "commandId", referencedColumnName = "id")
    private Command command;
    public Payment() {
    }
    public Payment(double amount, Date paymentDate, CommandStatus commandStatus) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.CommandStatus = commandStatus;
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
    public com.etherea.enums.CommandStatus getCommandStatus() {
        return CommandStatus;
    }
    public void setCommandStatus(com.etherea.enums.CommandStatus commandStatus) {
        CommandStatus = commandStatus;
    }
}

