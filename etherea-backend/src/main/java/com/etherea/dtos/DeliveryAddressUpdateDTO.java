package com.etherea.dtos;

import com.etherea.models.DeliveryAddress;

public class DeliveryAddressUpdateDTO {
    private Long id;
    private String address;
    private String city;
    private int zipCode;
    private String country;
    private String phoneNumber;
    private boolean isDefault;

    public DeliveryAddressUpdateDTO() {}

    public DeliveryAddressUpdateDTO(Long id, String address, String city, int zipCode, String country, String phoneNumber, boolean isDefault) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.isDefault = isDefault;
    }

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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public static DeliveryAddressUpdateDTO fromDeliveryAddress(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressUpdateDTO(
                deliveryAddress.getId(),
                deliveryAddress.getAddress(),
                deliveryAddress.getCity(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getCountry(),
                deliveryAddress.getPhoneNumber(),
                deliveryAddress.isDefault()
        );
    }

    public DeliveryAddress toDeliveryAddress() {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setId(this.id);
        deliveryAddress.setAddress(this.address);
        deliveryAddress.setCity(this.city);
        deliveryAddress.setZipCode(this.zipCode);
        deliveryAddress.setCountry(this.country);
        deliveryAddress.setPhoneNumber(this.phoneNumber);
        deliveryAddress.setDefault(this.isDefault);
        return deliveryAddress;
    }
}
