package com.etherea.models;

import jakarta.persistence.*;

@Entity
public class CommandItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;  //Quantity of products in order
    private double unitPrice;
    private double totalPrice;
    private String productName;
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private Product product;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "commandId", referencedColumnName = "id")
    private Command command;
    public CommandItem() {
    }
    public CommandItem(int quantity, double unitPrice, Product product, Command command, String productName) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.product = product;
        this.command = command;
        this.productName = productName;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Command getCommand() {
        return command;
    }
    public void setCommand(Command command) {
        this.command = command;
    }
    public void calculateTotalPrice() {
        this.totalPrice = this.quantity * this.unitPrice;
    }
}