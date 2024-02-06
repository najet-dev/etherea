package com.etherea.models;

import jakarta.persistence.*;

@Entity
public class CommandItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double unitPrice;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "commandId")
    private Command command;
}
