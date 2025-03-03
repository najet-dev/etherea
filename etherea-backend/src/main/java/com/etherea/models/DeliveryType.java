package com.etherea.models;

import com.etherea.enums.DeliveryName;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

@Entity
public class DeliveryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private DeliveryName deliveryName;
    @Column(nullable = false)
    @Min(1)
    private int deliveryDays;
    @Column(nullable = false, precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal cost;
    public DeliveryType() {}
    public DeliveryType(DeliveryName deliveryName, int deliveryDays, BigDecimal cost) {
        this.deliveryName = deliveryName;
        this.deliveryDays = deliveryDays;
        this.cost = cost;
    }

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }
    public DeliveryName getDeliveryName() { return deliveryName; }
    public void setDeliveryName(DeliveryName deliveryName) { this.deliveryName = deliveryName; }
    public int getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(int deliveryDays) { this.deliveryDays = deliveryDays; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
}
