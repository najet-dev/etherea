package com.etherea.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class DeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    private int zipCode;
    private String city;
    private String country;
    private String phoneNumber;
    private boolean isDefault;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "deliveryAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomeExpressDelivery> expressDeliveries = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomeStandardDelivery> standardDeliveries = new ArrayList<>();
    public DeliveryAddress() {}
    public DeliveryAddress(String address, int zipCode, String city, String country, String phoneNumber, boolean isDefault, User user) {
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.isDefault = isDefault;
        this.user = user;
    }
    // Getters et Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public int getZipCode() {
        return zipCode;
    }
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public boolean isDefault() {
        return isDefault;
    }
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<HomeExpressDelivery> getExpressDeliveries() {
        return expressDeliveries;
    }
    public void setExpressDeliveries(List<HomeExpressDelivery> expressDeliveries) {
        this.expressDeliveries = expressDeliveries;
    }
    public List<HomeStandardDelivery> getStandardDeliveries() {
        return standardDeliveries;
    }
    public void setStandardDeliveries(List<HomeStandardDelivery> standardDeliveries) {
        this.standardDeliveries = standardDeliveries;
    }
    // Dans DeliveryAddress.java
    public String getFullAddress() {
        return address + ", " + city + ", " + zipCode + ", " + country;
    }

}
