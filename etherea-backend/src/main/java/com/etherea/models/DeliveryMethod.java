package com.etherea.models;

import com.etherea.enums.DeliveryType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType type;
    @Column(nullable = false)
    private int deliveryDays;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    @Embedded
    private PickupPointDetails pickupPointDetails;
    private static final Set<DeliveryType> HOME_DELIVERY_TYPES = Set.of(DeliveryType.HOME_STANDARD, DeliveryType.HOME_EXPRESS);
    public DeliveryMethod() {}
    public DeliveryMethod(DeliveryType type, int deliveryDays, BigDecimal deliveryCost, User user, DeliveryAddress deliveryAddress, PickupPointDetails pickupPointDetails) {
        this.type = type;
        this.deliveryDays = deliveryDays;
        this.cost = deliveryCost;
        this.user = user;
        this.deliveryAddress = deliveryAddress;
        this.pickupPointDetails = pickupPointDetails;
    }

    public boolean isPickupPoint() {
        return this.type == DeliveryType.PICKUP_POINT;
    }
    public boolean isHomeDelivery() {
        return HOME_DELIVERY_TYPES.contains(this.type);
    }
    public void changeDeliveryType(DeliveryType newType, DeliveryAddress newAddress, PickupPointDetails newPickupDetails) {
        this.type = newType;
        this.deliveryAddress = (newType == DeliveryType.PICKUP_POINT) ? null : newAddress;
        this.pickupPointDetails = (newType == DeliveryType.PICKUP_POINT) ? newPickupDetails : null;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DeliveryType getType() { return type; }
    public void setType(DeliveryType type) { this.type = type; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) {
        this.cost = (cost != null && cost.compareTo(BigDecimal.ZERO) >= 0) ? cost : BigDecimal.ZERO;
    }
    public int getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(int deliveryDays) {
        if (deliveryDays < 0) {
            throw new IllegalArgumentException("Le nombre de jours de livraison ne peut pas être négatif.");
        }
        this.deliveryDays = deliveryDays;
    }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public PickupPointDetails getPickupPointDetails() { return pickupPointDetails; }
    public void setPickupPointDetails(PickupPointDetails pickupPointDetails) { this.pickupPointDetails = pickupPointDetails; }
}
