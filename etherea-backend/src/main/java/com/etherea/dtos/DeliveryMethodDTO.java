package com.etherea.dtos;

import com.etherea.enums.DeliveryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryType deliveryType;
    private LocalDate expectedDeliveryDate;
    private BigDecimal cost;
    private DeliveryAddressDTO deliveryAddress;
    private PickupPointDetailsDTO pickupPointDetails;
    public DeliveryMethodDTO() {}
    public DeliveryMethodDTO(Long id, DeliveryType deliveryType, LocalDate expectedDeliveryDate,
                             BigDecimal cost, DeliveryAddressDTO deliveryAddress,
                             PickupPointDetailsDTO pickupPointDetails) {
        this.id = id;
        this.deliveryType = deliveryType;
        this.expectedDeliveryDate = validateExpectedDeliveryDate(expectedDeliveryDate);
        this.cost = cost != null ? cost : BigDecimal.ZERO;
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
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = validateExpectedDeliveryDate(expectedDeliveryDate);
    }
    public BigDecimal getCost() {
        return cost;
    }
    public void setCost(BigDecimal cost) {
        this.cost = cost != null ? cost : BigDecimal.ZERO;
    }
    public Optional<DeliveryAddressDTO> getDeliveryAddress() {
        return Optional.ofNullable(deliveryAddress);
    }
    public void setDeliveryAddress(DeliveryAddressDTO deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public Optional<PickupPointDetailsDTO> getPickupPointDetails() {
        return Optional.ofNullable(pickupPointDetails);
    }
    public void setPickupPointDetails(PickupPointDetailsDTO pickupPointDetails) {
        this.pickupPointDetails = pickupPointDetails;
    }

    // Vérifie que la date de livraison prévue n'est pas dans le passé
    private LocalDate validateExpectedDeliveryDate(LocalDate date) {
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de livraison prévue ne peut pas être dans le passé.");
        }
        return date;
    }
}
