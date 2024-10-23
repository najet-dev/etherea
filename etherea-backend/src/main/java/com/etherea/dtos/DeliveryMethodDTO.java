package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import com.etherea.models.PickupPoint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryOption deliveryOption;
    private PickupPointDTO pickupPoint; // Utilisation du DTO pour PickupPoint
    private LocalDate expectedDeliveryDate;
    public DeliveryMethodDTO() {}
    public DeliveryMethodDTO(Long id, DeliveryOption deliveryOption, PickupPointDTO pickupPoint, LocalDate expectedDeliveryDate) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.pickupPoint = pickupPoint;
        this.expectedDeliveryDate = expectedDeliveryDate;
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
    // Méthode de conversion de l'entité vers le DTO
    public static DeliveryMethodDTO fromDeliveryMethod(DeliveryMethod deliveryMethod) {
        return new DeliveryMethodDTO(
                deliveryMethod.getId(),
                deliveryMethod.getDeliveryOption(),
                deliveryMethod.getPickupPoint() != null ? PickupPointDTO.fromPickupPoint(deliveryMethod.getPickupPoint()) : null,
                deliveryMethod.getExpectedDeliveryDate()
        );
    }
    // Méthode de conversion du DTO vers l'entité
    public DeliveryMethod toDeliveryMethod() {
        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setId(this.id);
        deliveryMethod.setDeliveryOption(this.deliveryOption);
        deliveryMethod.setPickupPoint(this.pickupPoint != null ? this.pickupPoint.toPickupPoint() : null);
        deliveryMethod.setExpectedDeliveryDate(this.expectedDeliveryDate);
        return deliveryMethod;
    }
}
