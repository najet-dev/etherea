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
    @OneToOne
    @JoinColumn(name = "commandId")
    private Command command;
}