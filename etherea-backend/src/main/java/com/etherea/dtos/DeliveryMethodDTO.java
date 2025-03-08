package com.etherea.dtos;

import com.etherea.models.DeliveryMethod;
import com.etherea.models.User;

import java.util.Objects;
import java.util.Optional;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryTypeDTO deliveryType;
    private Long userId;
    private DeliveryAddressDTO deliveryAddress;
    private PickupPointDetailsDTO pickupPointDetails;
    public DeliveryMethodDTO() {}
    public DeliveryMethodDTO(Long id, DeliveryTypeDTO deliveryType, Long userId, DeliveryAddressDTO deliveryAddress, PickupPointDetailsDTO pickupPointDetails) {
        this.id = id;
        this.deliveryType = deliveryType;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.pickupPointDetails = pickupPointDetails;
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
    public DeliveryAddressDTO getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(DeliveryAddressDTO deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public PickupPointDetailsDTO getPickupPointDetails() {
        return pickupPointDetails;
    }
    public void setPickupPointDetails(PickupPointDetailsDTO pickupPointDetails) {
        this.pickupPointDetails = pickupPointDetails;
    }
    public static DeliveryMethodDTO fromEntity(DeliveryMethod deliveryMethod) {
        Objects.requireNonNull(deliveryMethod, "Le mode de livraison ne peut pas être null.");

        return new DeliveryMethodDTO(
                deliveryMethod.getId(),
                DeliveryTypeDTO.fromEntity(deliveryMethod.getDeliveryType()),
                Optional.ofNullable(deliveryMethod.getUser()).map(User::getId).orElse(null),
                Optional.ofNullable(deliveryMethod.getDeliveryAddress()).map(DeliveryAddressDTO::fromDeliveryAddress).orElse(null),
                Optional.ofNullable(deliveryMethod.getPickupPointDetails()).map(PickupPointDetailsDTO::fromEntity).orElse(null)
        );
    }
    public DeliveryMethod toEntity() {
        return new DeliveryMethod(
                deliveryType.toEntity(),
                null,
                deliveryAddress != null ? deliveryAddress.toDeliveryAddress() : null,
                pickupPointDetails != null ? pickupPointDetails.toEntity() : null
        );
    }
}
