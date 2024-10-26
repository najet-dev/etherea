package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import java.time.LocalDate;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryOption deliveryOption;
    private PickupPointDTO pickupPoint;
    private LocalDate expectedDeliveryDate;
    private Double cost;
    private Double minimumAmountForFreeDelivery;
    public DeliveryMethodDTO() {}
    public DeliveryMethodDTO(Long id, DeliveryOption deliveryOption, PickupPointDTO pickupPoint, LocalDate expectedDeliveryDate, Double cost, Double  minimumAmountForFreeDelivery) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.pickupPoint = pickupPoint;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.cost = cost;
        this.minimumAmountForFreeDelivery = minimumAmountForFreeDelivery;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public DeliveryOption getDeliveryOption() {
        return deliveryOption;
    }
    public void setDeliveryOption(DeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
    }
    public PickupPointDTO getPickupPoint() {
        return pickupPoint;
    }
    public void setPickupPoint(PickupPointDTO pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
    }
    public Double getMinimumAmountForFreeDelivery() {
        return minimumAmountForFreeDelivery;
    }
    public void setMinimumAmountForFreeDelivery(Double minimumAmountForFreeDelivery) {
        this.minimumAmountForFreeDelivery = minimumAmountForFreeDelivery;
    }

    // Conversion de l'entité vers le DTO
    public static DeliveryMethodDTO fromDeliveryMethod(DeliveryMethod deliveryMethod) {
        return new DeliveryMethodDTO(
                deliveryMethod.getId(),
                deliveryMethod.getDeliveryOption(),
                deliveryMethod.getPickupPoint() != null ? PickupPointDTO.fromPickupPoint(deliveryMethod.getPickupPoint()) : null,
                deliveryMethod.getExpectedDeliveryDate(),
                deliveryMethod.getCost(),
                deliveryMethod.getMinimumAmountForFreeDelivery()
        );
    }
    // Conversion du DTO vers l'entité
    public DeliveryMethod toDeliveryMethod() {
        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setId(this.id);
        deliveryMethod.setDeliveryOption(this.deliveryOption);
        deliveryMethod.setPickupPoint(this.pickupPoint != null ? this.pickupPoint.toPickupPoint() : null);
        deliveryMethod.setExpectedDeliveryDate(this.expectedDeliveryDate);
        deliveryMethod.setCost(this.cost);
        deliveryMethod.setMinimumAmountForFreeDelivery(this.minimumAmountForFreeDelivery);
        return deliveryMethod;
    }
}
