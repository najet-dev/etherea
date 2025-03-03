package com.etherea.models;

import jakarta.persistence.*;

@Entity
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_type_id", nullable = false)
    private DeliveryType deliveryType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_point_id")
    private PickupPointDetails pickupPointDetails;
    public DeliveryMethod() {
    }
    public DeliveryMethod(DeliveryType deliveryType, User user, DeliveryAddress deliveryAddress, PickupPointDetails pickupPointDetails) {
        this.deliveryType = deliveryType;
        this.user = user;
        this.deliveryAddress = deliveryAddress;
        this.pickupPointDetails = pickupPointDetails;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public DeliveryType getDeliveryType() {
        return deliveryType;
    }
    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public PickupPointDetails getPickupPointDetails() {
        return pickupPointDetails;
    }
    public void setPickupPointDetails(PickupPointDetails pickupPointDetails) {
        this.pickupPointDetails = pickupPointDetails;
    }
}
