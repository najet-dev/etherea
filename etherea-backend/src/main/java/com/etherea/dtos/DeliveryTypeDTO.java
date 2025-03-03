package com.etherea.dtos;

import com.etherea.enums.DeliveryName;
import com.etherea.models.DeliveryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class DeliveryTypeDTO {
    private Long id;
    private DeliveryName deliveryName;
    @Positive(message = "The number of delivery days must be positive.")
    private int deliveryDays;
    @NotNull(message = "The delivery cost cannot be null.")
    private BigDecimal cost;
    private LocalDate estimatedDeliveryDate;
    public DeliveryTypeDTO() {
    }
    public DeliveryTypeDTO(Long id, DeliveryName deliveryName, int deliveryDays, BigDecimal cost, LocalDate estimatedDeliveryDate) {
        this.id = id;
        this.deliveryName = deliveryName;
        this.deliveryDays = deliveryDays;
        this.cost = cost;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }
    public int getDeliveryDays() { return deliveryDays; }
    public DeliveryName getDeliveryName() {
        return deliveryName;
    }
    public void setDeliveryName(DeliveryName deliveryName) {
        this.deliveryName = deliveryName;
    }
    public void setDeliveryDays(int deliveryDays) { this.deliveryDays = deliveryDays; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
    public static DeliveryTypeDTO fromEntity(DeliveryType deliveryType) {
        Objects.requireNonNull(deliveryType, "The delivery type cannot be null.");
        return new DeliveryTypeDTO(
                deliveryType.getId(),
                deliveryType.getDeliveryName(),
                deliveryType.getDeliveryDays(),
                deliveryType.getCost(),
                LocalDate.now().plusDays(deliveryType.getDeliveryDays())
        );
    }
    public DeliveryType toEntity() {
        return new DeliveryType(deliveryName, deliveryDays, cost);
    }
}
