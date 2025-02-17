package com.etherea.models;

import com.etherea.enums.DeliveryType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "delivery_methods")
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType type;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;
    @Column(nullable = false)
    private int deliveryDays;
    @Column(nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    @Embedded
    private PickupPointDetails pickupPointDetails;
    public DeliveryMethod() {}
    public DeliveryMethod(DeliveryType type, BigDecimal cost, int deliveryDays, String description, User user) {
        this.type = type;
        this.cost = cost;
        this.deliveryDays = deliveryDays;
        this.description = description;
        this.user = user;
    }
    public boolean isPickupPoint() {
        return this.type == DeliveryType.PICKUP_POINT;
    }
    public boolean isHomeDelivery() {
        return this.type == DeliveryType.HOME_STANDARD || this.type == DeliveryType.HOME_EXPRESS;
    }
    public void changeDeliveryType(DeliveryType newType, DeliveryAddress newAddress, PickupPointDetails newPickupDetails) {
        this.type = newType;
        if (newType == DeliveryType.PICKUP_POINT) {
            this.deliveryAddress = null;
            this.pickupPointDetails = newPickupDetails;
        } else {
            this.pickupPointDetails = null;
            this.deliveryAddress = newAddress;
        }
    }
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public DeliveryType getType() { return type; }

    public void setType(DeliveryType type) { this.type = type; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public int getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(int deliveryDays) { this.deliveryDays = deliveryDays; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
    public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public PickupPointDetails getPickupPointDetails() { return pickupPointDetails; }
    public void setPickupPointDetails(PickupPointDetails pickupPointDetails) { this.pickupPointDetails = pickupPointDetails; }
}
