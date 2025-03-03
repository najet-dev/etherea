package com.etherea.repositories;

import com.etherea.enums.DeliveryName;
import com.etherea.models.DeliveryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface DeliveryTypeRepository extends JpaRepository<DeliveryType, Long> {
    Optional<DeliveryType> findByDeliveryName(DeliveryName deliveryName);
}
