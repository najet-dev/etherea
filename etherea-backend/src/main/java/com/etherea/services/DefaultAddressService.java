package com.etherea.services;

import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.models.DeliveryAddress;
import com.etherea.repositories.DeliveryAddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing the default delivery address of a user.
 */
@Service
public class DefaultAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;
    public DefaultAddressService(DeliveryAddressRepository deliveryAddressRepository ) {
        this.deliveryAddressRepository = deliveryAddressRepository;
    }

    /**
     * Sets a specific delivery address as the default for a given user.
     * All other addresses for the user will be marked as non-default.
     *
     * @param userId    The ID of the user.
     * @param addressId The ID of the address to be set as default.
     * @throws DeliveryAddressNotFoundException if the address does not belong to the user.
     */
    public void setDefaultAddress(Long userId, Long addressId) {
        // Retrieve all addresses for the user
        List<DeliveryAddress> addresses = deliveryAddressRepository.findByUserId(userId);

        // Verify that the specified address belongs to the user
        boolean addressBelongsToUser = addresses.stream()
                .anyMatch(address -> address.getId().equals(addressId));

        if (!addressBelongsToUser) {
            throw new DeliveryAddressNotFoundException("The address does not belong to the specified user.");
        }

        // Update the default status: only the selected address is marked as default
        for (DeliveryAddress address : addresses) {
            address.setDefault(address.getId().equals(addressId));
        }

        // Save all updated addresses
        deliveryAddressRepository.saveAll(addresses);
    }
}
