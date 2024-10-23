package com.etherea.repositories;

import com.etherea.models.Command;
import com.etherea.models.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    List<DeliveryAddress> findByUserId(Long userId);
    Optional<DeliveryAddress> findTopByUserIdOrderByIdDesc(Long userId);


}
