package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryMethod;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryOption deliveryOption;
    private List<PickupPointDTO> pickupPoints;
    private LocalDate expectedDeliveryDate;
    private Double cost;
    private Double minimumAmountForFreeDelivery;
    public DeliveryMethodDTO() {}
    public DeliveryMethodDTO(Long id, DeliveryOption deliveryOption, List<PickupPointDTO> pickupPoints,
                             LocalDate expectedDeliveryDate, Double cost, Double minimumAmountForFreeDelivery) {
        this.id = id;
        this.deliveryOption = deliveryOption;
        this.pickupPoints = Optional.ofNullable(pickupPoints).orElse(Collections.emptyList());
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.cost = cost;
        this.minimumAmountForFreeDelivery = minimumAmountForFreeDelivery;
    }
    // Getters et Setters ...
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
    public List<PickupPointDTO> getPickupPoints() {
        return pickupPoints;
    }
    public void setPickupPoints(List<PickupPointDTO> pickupPoints) {
        this.pickupPoints = pickupPoints;
    }
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
    }
    public Double getMinimumAmountForFreeDelivery() {
        return minimumAmountForFreeDelivery;
    }
    public void setMinimumAmountForFreeDelivery(Double minimumAmountForFreeDelivery) {
        this.minimumAmountForFreeDelivery = minimumAmountForFreeDelivery;
    }
    /**
     * Convertit une entité DeliveryMethod en DeliveryMethodDTO.
     * @param deliveryMethod l'entité DeliveryMethod
     * @return un DeliveryMethodDTO correspondant
     */
    public static DeliveryMethodDTO fromDeliveryMethod(DeliveryMethod deliveryMethod) {
        List<PickupPointDTO> pickupPointDTOs = Optional.ofNullable(deliveryMethod.getPickupPoints())
                .orElse(Collections.emptyList())
                .stream()
                .map(PickupPointDTO::fromPickupPoint)
                .collect(Collectors.toList());

        return new DeliveryMethodDTO(
                deliveryMethod.getId(),
                deliveryMethod.getDeliveryOption(),
                pickupPointDTOs,
                deliveryMethod.getExpectedDeliveryDate(),
                deliveryMethod.getCost(),
                deliveryMethod.getMinimumAmountForFreeDelivery()
        );
    }

    /**
     * Convertit ce DeliveryMethodDTO en entité DeliveryMethod.
     * @return l'entité DeliveryMethod correspondante
     */
    public DeliveryMethod toDeliveryMethod() {
        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setId(this.id);
        deliveryMethod.setDeliveryOption(this.deliveryOption);
        deliveryMethod.setPickupPoints(this.pickupPoints.stream()
                .map(PickupPointDTO::toPickupPoint)
                .collect(Collectors.toList()));
        deliveryMethod.setExpectedDeliveryDate(this.expectedDeliveryDate);
        deliveryMethod.setCost(this.cost);
        deliveryMethod.setMinimumAmountForFreeDelivery(this.minimumAmountForFreeDelivery);
        return deliveryMethod;
    }
}
