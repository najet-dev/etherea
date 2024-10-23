package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DeliveryOption deliveryOption;
    @ManyToOne
    @JoinColumn(name = "pickup_point_id")
    private PickupPoint pickupPoint;
    private LocalDate expectedDeliveryDate;
    public DeliveryMethod() {}
    public DeliveryMethod(Long id, DeliveryOption deliveryOption, PickupPoint pickupPoint, LocalDate expectedDeliveryDate) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.pickupPoint = pickupPoint;
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
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
    public PickupPoint getPickupPoint() {
        return pickupPoint;
    }
    public void setPickupPoint(PickupPoint pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
}
