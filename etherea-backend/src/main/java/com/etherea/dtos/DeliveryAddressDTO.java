package com.etherea.dtos;

import com.etherea.models.DeliveryAddress;

public class DeliveryAddressDTO {
    private Long id;
    private String address;
    private String city;
    private int zipCode;
    private String country;
    private String phoneNumber;
    private UserDTO user;

    // Constructeurs
    public DeliveryAddressDTO() {}

    public DeliveryAddressDTO(Long id, String address, String city, int zipCode, String country, String phoneNumber, UserDTO user) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.phoneNumber = phoneNumber;
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
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public int getZipCode() {
        return zipCode;
    }
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
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
    public UserDTO getUser() {
        return user;
    }
    public void setUser(UserDTO user) {
        this.user = user;
    }

    // Convertir un objet DeliveryAddress en DeliveryAddressDTO
    public static DeliveryAddressDTO fromDeliveryAddress(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressDTO(
                deliveryAddress.getId(),
                deliveryAddress.getAddress(),
                deliveryAddress.getCity(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getCountry(),
                deliveryAddress.getPhoneNumber(),
                UserDTO.fromUser(deliveryAddress.getUser())  // Conversion de User vers UserDTO
        );
    }

    // Convertir un objet DeliveryAddressDTO en DeliveryAddress
    public DeliveryAddress toDeliveryAddress() {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setId(this.id);
        deliveryAddress.setAddress(this.address);
        deliveryAddress.setCity(this.city);
        deliveryAddress.setZipCode(this.zipCode);
        deliveryAddress.setCountry(this.country);
        deliveryAddress.setPhoneNumber(this.phoneNumber);
        // Si user n'est pas null, l'associer à l'adresse de livraison
        if (this.user != null) {
            deliveryAddress.setUser(this.user.toUser());
        }
        return deliveryAddress;
    }
}
