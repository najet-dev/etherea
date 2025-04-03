package com.etherea.dtos;

import com.etherea.models.CommandItem;

public class CommandItemDTO {
    private Long id;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public CommandItemDTO() {}

    public CommandItemDTO(Long id, String productName, int quantity, double unitPrice, double totalPrice) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    // Méthode de conversion depuis CommandItem
    public static CommandItemDTO fromCommandItem(CommandItem commandItem) {
        return new CommandItemDTO(
                commandItem.getId(),
                commandItem.getProductName(),
                commandItem.getQuantity(),
                commandItem.getUnitPrice(),
                commandItem.getTotalPrice()
        );
    }
}