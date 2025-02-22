package com.etherea.dtos;

import com.etherea.enums.DeliveryType;
import com.etherea.models.DeliveryMethod;
import com.etherea.utils.DeliveryDateCalculator;
import com.etherea.utils.DeliveryCostCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryType type;
    private LocalDate deliveryDays;
    private BigDecimal cost;
    private DeliveryAddressDTO deliveryAddress;
    private PickupPointDetailsDTO pickupPointDetails;

    public DeliveryMethodDTO() {}

    public DeliveryMethodDTO(Long id, DeliveryType type, LocalDate deliveryDays, BigDecimal cost, DeliveryAddressDTO deliveryAddress, PickupPointDetailsDTO pickupPointDetails) {
        this.id = id;
        this.type = type;
        this.deliveryDays = deliveryDays;
        this.cost = cost;
        this.deliveryAddress = deliveryAddress;
        this.pickupPointDetails = pickupPointDetails;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DeliveryType getType() {
        return type;
    }
    public void setType(DeliveryType type) {
        this.type = type;
    }
    public LocalDate getDeliveryDays() {
        return deliveryDays;
    }
    public void setDeliveryDays(LocalDate deliveryDays) {
        this.deliveryDays = deliveryDays;
    }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public DeliveryAddressDTO getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(DeliveryAddressDTO deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public PickupPointDetailsDTO getPickupPointDetails() { return pickupPointDetails; }
    public void setPickupPointDetails(PickupPointDetailsDTO pickupPointDetails) { this.pickupPointDetails = pickupPointDetails; }
    public static DeliveryMethodDTO fromDeliveryMethod(DeliveryMethod deliveryMethod, LocalDate startDate, BigDecimal cartTotal, DeliveryDateCalculator calculator) {
        if (deliveryMethod == null || calculator == null) {
            throw new IllegalArgumentException("Les paramètres deliveryMethod et calculator ne peuvent pas être nuls.");
        }

        DeliveryType type = deliveryMethod.getType();

        // Calcul de la date de livraison estimée
        LocalDate deliveryDays = calculator.calculateDeliveryDate(startDate, deliveryMethod);

        // Calcul du coût de livraison
        BigDecimal cost = DeliveryCostCalculator.calculateDeliveryCost(cartTotal, deliveryMethod);

        // Conversion de l'adresse si applicable
        DeliveryAddressDTO addressDTO = (deliveryMethod.isHomeDelivery() && deliveryMethod.getDeliveryAddress() != null)
                ? DeliveryAddressDTO.fromDeliveryAddress(deliveryMethod.getDeliveryAddress())
                : null;

        // Conversion du point relais si applicable
        PickupPointDetailsDTO pickupDTO = (deliveryMethod.isPickupPoint() && deliveryMethod.getPickupPointDetails() != null)
                ? new PickupPointDetailsDTO(
                deliveryMethod.getPickupPointDetails().getPickupPointName(),
                deliveryMethod.getPickupPointDetails().getPickupPointAddress(),
                deliveryMethod.getPickupPointDetails().getPickupPointLatitude(),
                deliveryMethod.getPickupPointDetails().getPickupPointLongitude()
        )
                : null;

        return new DeliveryMethodDTO(
                deliveryMethod.getId(),
                type,
                deliveryDays,
                cost,
                addressDTO,
                pickupDTO
        );
    }
    public static DeliveryType convertDeliveryTypeToOption(DeliveryType type) {
        return switch (type) {
            case HOME_STANDARD -> DeliveryType.HOME_STANDARD;
            case HOME_EXPRESS -> DeliveryType.HOME_EXPRESS;
            case PICKUP_POINT -> DeliveryType.PICKUP_POINT;
        };
    }
}
