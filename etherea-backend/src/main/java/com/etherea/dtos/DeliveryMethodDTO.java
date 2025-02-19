package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import com.etherea.enums.DeliveryType;
import com.etherea.models.DeliveryMethod;
import com.etherea.utils.DeliveryDateCalculator;
import com.etherea.utils.DeliveryCostCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DeliveryMethodDTO {
    private Long id;
    private DeliveryType type;
    private LocalDate expectedDeliveryDate;
    private BigDecimal cost;
    private DeliveryAddressDTO deliveryAddress;
    private PickupPointDetailsDTO pickupPointDetails;

    public DeliveryMethodDTO() {}

    public DeliveryMethodDTO(Long id, DeliveryType type, LocalDate expectedDeliveryDate, BigDecimal cost, DeliveryAddressDTO deliveryAddress, PickupPointDetailsDTO pickupPointDetails) {
        this.id = id;
        this.type = type;
        this.expectedDeliveryDate = expectedDeliveryDate;
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
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
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

        // On garde DeliveryType, il n'est pas nécessaire de convertir ici.
        DeliveryType type = deliveryMethod.getType(); // Pas de conversion ici

        // Calcul de la date de livraison estimée
        LocalDate expectedDeliveryDate = calculator.calculateDeliveryDate(startDate, deliveryMethod);

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
                type,  // Utilisez simplement DeliveryType ici
                expectedDeliveryDate,
                cost,
                addressDTO,
                pickupDTO
        );
    }
    public static DeliveryOption convertDeliveryTypeToOption(DeliveryType type) {
        return switch (type) {
            case HOME_STANDARD -> DeliveryOption.HOME_STANDARD;
            case HOME_EXPRESS -> DeliveryOption.HOME_EXPRESS;
            case PICKUP_POINT -> DeliveryOption.PICKUP_POINT;
        };
    }

}
