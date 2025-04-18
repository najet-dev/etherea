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
    private boolean isDefault;
    public DeliveryAddressDTO() {}
    public DeliveryAddressDTO(Long id, String address, String city, int zipCode, String country, String phoneNumber, boolean isDefault, UserDTO user) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.isDefault = isDefault;
        this.user = user;
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
    public UserDTO getUser() {
        return user;
    }
    public void setUser(UserDTO user) {
        this.user = user;
    }
    public boolean isDefault() {
        return isDefault;
    }
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Convert a DeliveryAddress object to DeliveryAddressDTO
    public static DeliveryAddressDTO fromDeliveryAddress(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressDTO(
                deliveryAddress.getId(),
                deliveryAddress.getAddress(),
                deliveryAddress.getCity(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getCountry(),
                deliveryAddress.getPhoneNumber(),
                deliveryAddress.isDefault(),
                UserDTO.fromUser(deliveryAddress.getUser())  // Converting User to UserDTO
        );
    }
    // Convert a DeliveryAddressDTO object to DeliveryAddress
    public DeliveryAddress toDeliveryAddress() {
        DeliveryAddress deliveryAddress = new DeliveryAddress();

        if (this.id != null) {
            deliveryAddress.setId(this.id);
        }

        deliveryAddress.setAddress(this.address);
        deliveryAddress.setCity(this.city);
        deliveryAddress.setZipCode(this.zipCode);
        deliveryAddress.setCountry(this.country);
        deliveryAddress.setPhoneNumber(this.phoneNumber);
        deliveryAddress.setDefault(this.isDefault);

        if (this.user != null) {
            deliveryAddress.setUser(this.user.toUser());
        }

        return deliveryAddress;
    }

}
