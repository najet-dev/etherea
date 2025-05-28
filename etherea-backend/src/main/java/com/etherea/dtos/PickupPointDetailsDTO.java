package com.etherea.dtos;

import com.etherea.models.PickupPointDetails;
import jakarta.validation.constraints.NotBlank;
public class PickupPointDetailsDTO {
    private Long id;
    @NotBlank(message = "Le nom du point de retrait est obligatoire.")
    private String pickupPointName;
    @NotBlank(message = "L'adresse du point de retrait est obligatoire.")
    private String pickupPointAddress;
    private Double pickupPointLatitude;
    private Double pickupPointLongitude;
    public PickupPointDetailsDTO() {}
    public PickupPointDetailsDTO(Long id, String pickupPointName, String pickupPointAddress, Double pickupPointLatitude, Double pickupPointLongitude) {
        this.id = id;
        this.pickupPointName = pickupPointName;
        this.pickupPointAddress = pickupPointAddress;
        this.pickupPointLatitude = pickupPointLatitude;
        this.pickupPointLongitude = pickupPointLongitude;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPickupPointName() {
        return pickupPointName;
    }
    public void setPickupPointName(String pickupPointName) {
        this.pickupPointName = pickupPointName;
    }
    public String getPickupPointAddress() {
        return pickupPointAddress;
    }
    public void setPickupPointAddress(String pickupPointAddress) {
        this.pickupPointAddress = pickupPointAddress;
    }
    public Double getPickupPointLatitude() {
        return pickupPointLatitude;
    }
    public void setPickupPointLatitude(Double pickupPointLatitude) {
        this.pickupPointLatitude = pickupPointLatitude;
    }
    public Double getPickupPointLongitude() {
        return pickupPointLongitude;
    }
    public void setPickupPointLongitude(Double pickupPointLongitude) {
        this.pickupPointLongitude = pickupPointLongitude;
    }
    public static PickupPointDetailsDTO fromEntity(PickupPointDetails pickupPointDetails) {
        if (pickupPointDetails == null) return null;
        return new PickupPointDetailsDTO(
                pickupPointDetails.getId(),
                pickupPointDetails.getPickupPointName(),
                pickupPointDetails.getPickupPointAddress(),
                pickupPointDetails.getPickupPointLatitude(),
                pickupPointDetails.getPickupPointLongitude()
        );
    }
    public PickupPointDetails toEntity() {
        return new PickupPointDetails(pickupPointName, pickupPointAddress, pickupPointLatitude, pickupPointLongitude);
    }
}
