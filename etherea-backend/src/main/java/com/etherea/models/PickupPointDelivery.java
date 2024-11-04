package com.etherea.models;

import com.etherea.enums.DeliveryOption;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

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
        this.name = Objects.requireNonNull(name, "Le nom ne peut pas être nul.");
        this.address = Objects.requireNonNull(address, "L'adresse ne peut pas être nulle.");
        setLatitude(latitude);
        setLongitude(longitude);
    }

    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90.");
        }
        this.latitude = latitude;
    }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) {
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180.");
        }
        this.longitude = longitude;
    }
    @Override
    public LocalDate calculateExpectedDeliveryDate() {
        return LocalDate.now().plusDays(DELIVERY_DAYS); // Retourne la date de livraison prévue
    }
    @Override
    public Double calculateCost(Double orderAmount) {
        if (orderAmount == null || orderAmount < 0) {
            throw new IllegalArgumentException("Le montant de la commande doit être non négatif.");
        }
        return (orderAmount < FREE_SHIPPING_THRESHOLD) ? SHIPPING_COST : 0.0; // Retourne le coût basé sur le montant de la commande
    }
}
