package com.etherea.services;

import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.models.DeliveryAddress;
import com.etherea.repositories.DeliveryAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DefaultAddressService {
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    // Met à jour l'adresse par défaut pour un utilisateur donné
    public void setDefaultAddress(Long userId, Long addressId) {
        List<DeliveryAddress> addresses = deliveryAddressRepository.findByUserId(userId);
        boolean addressBelongsToUser = addresses.stream()
                .anyMatch(address -> address.getId().equals(addressId));

        if (!addressBelongsToUser) {
            throw new DeliveryAddressNotFoundException("L'adresse ne correspond pas à l'utilisateur.");
        }

        for (DeliveryAddress address : addresses) {
            address.setDefault(address.getId().equals(addressId));
        }

        deliveryAddressRepository.saveAll(addresses);
    }

}
