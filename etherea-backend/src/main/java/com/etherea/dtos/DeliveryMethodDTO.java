package com.etherea.dto;

import com.etherea.enums.DeliveryOption;
import java.time.LocalDate;

public abstract class DeliveryMethodDTO {
    private Long id;
    private DeliveryOption deliveryOption;
    private LocalDate expectedDeliveryDate;
    private Double cost;

    public DeliveryMethodDTO() {}

    public DeliveryMethodDTO(Long id, DeliveryOption deliveryOption, LocalDate expectedDeliveryDate, Double cost) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.cost = cost;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DeliveryOption getDeliveryOption() { return deliveryOption; }
    public void setDeliveryOption(DeliveryOption deliveryOption) { this.deliveryOption = deliveryOption; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}
