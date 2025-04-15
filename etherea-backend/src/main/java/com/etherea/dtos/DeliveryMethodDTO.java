package com.etherea.dtos;

import com.etherea.models.DeliveryMethod;
import com.etherea.models.User;

import java.util.Objects;
import java.util.Optional;
public class DeliveryMethodDTO {
    private Long id;
    private DeliveryTypeDTO deliveryType;
    private Long userId;
    public DeliveryMethodDTO() {}
    public DeliveryMethodDTO(Long id, DeliveryTypeDTO deliveryType, Long userId) {
        this.id = id;
        this.deliveryType = deliveryType;
        this.userId = userId;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public DeliveryTypeDTO getDeliveryType() {
        return deliveryType;
    }
    public void setDeliveryType(DeliveryTypeDTO deliveryType) {
        this.deliveryType = deliveryType;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public static DeliveryMethodDTO fromEntity(DeliveryMethod deliveryMethod) {
        Objects.requireNonNull(deliveryMethod, "Delivery mode cannot be null.");

        return new DeliveryMethodDTO(
                deliveryMethod.getId(),
                DeliveryTypeDTO.fromEntity(deliveryMethod.getDeliveryType()),
                Optional.ofNullable(deliveryMethod.getUser()).map(User::getId).orElse(null)
        );
    }
    public DeliveryMethod toEntity(User user) {
        return new DeliveryMethod(
                deliveryType.toEntity(),
                user
        );
    }
}
