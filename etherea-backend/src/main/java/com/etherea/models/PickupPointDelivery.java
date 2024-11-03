package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("PICKUP_POINT")
public class PickupPointDelivery extends DeliveryMethod {
    private static final Double FREE_SHIPPING_THRESHOLD = 50.0; // Seuil pour livraison gratuite
    private static final Double SHIPPING_COST = 3.0; // Coût de livraison standard
    private static final int DELIVERY_DAYS = 8; // Délai de livraison standard

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private Double latitude;
    private Double longitude;

    public PickupPointDelivery() {
        super(DeliveryOption.PICKUP_POINT, 0.0); // Initialise avec 0 par défaut
    }

    public PickupPointDelivery(String name, String address, Double latitude, Double longitude, Double orderAmount) {
        super(DeliveryOption.PICKUP_POINT, orderAmount); // Passer le montant de la commande à la classe parente
        this.name = name;
        this.address = address;
        setLatitude(latitude);
        setLongitude(longitude);
    }

    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(DELIVERY_DAYS); // Retourne la date de livraison prévue
    }

    @Override
    public Double calculateCost(Double orderAmount) {
        // Retourne le coût de la livraison basé sur le montant de la commande
        if (orderAmount != null) {
            return (orderAmount < FREE_SHIPPING_THRESHOLD) ? SHIPPING_COST : 0.0;
        }
        return 0.0; // Si orderAmount est nul, on peut retourner 0.0 par défaut
    }

    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90.");
        }
        this.latitude = latitude;
    }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) {
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180.");
        }
        this.longitude = longitude;
    }
}
