package com.etherea.dtos;

import com.etherea.models.DeliveryAddress;
import com.etherea.models.User;

public class DeliveryAddressDTO {
    private String address;
    private int zipCode;
    private String city;
    private String country;
    private String phoneNumber;
    private UserDTO user;

    public DeliveryAddressDTO() {
    }

    public DeliveryAddressDTO(String address, int zipCode, String city, String country, String phoneNumber, UserDTO user) {
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

    // Getters et Setters
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
    // Conversion de DeliveryAddress à DeliveryAddressDTO
    public static DeliveryAddressDTO fromDeliveryAddress(DeliveryAddress deliveryAddress) {
        if (deliveryAddress == null) {
            return null; // Gérer les cas de null
        }
        return new DeliveryAddressDTO(
                deliveryAddress.getAddress(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getCity(),
                deliveryAddress.getCountry(),
                deliveryAddress.getPhoneNumber(),
                UserDTO.fromUser(deliveryAddress.getUser()) // Conversion de User à UserDTO
        );
    }
    // Conversion de DeliveryAddressDTO à DeliveryAddress
    public DeliveryAddress toDeliveryAddress() {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setAddress(this.address);
        deliveryAddress.setZipCode(this.zipCode);
        deliveryAddress.setCity(this.city);
        deliveryAddress.setCountry(this.country);
        deliveryAddress.setPhoneNumber(this.phoneNumber);
        deliveryAddress.setUser(this.user.toUser()); // Conversion de UserDTO à User
        return deliveryAddress;
    }
}
