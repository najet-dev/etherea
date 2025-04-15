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
    public DeliveryMethod() {
    }
    public DeliveryMethod(DeliveryType deliveryType, User user) {
        this.deliveryType = deliveryType;
        this.user = user;
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
}