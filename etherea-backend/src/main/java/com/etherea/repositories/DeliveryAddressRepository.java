package com.etherea.repositories;

import com.etherea.models.Command;
import com.etherea.models.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository  extends JpaRepository<DeliveryAddress, Long> {
}
