package com.etherea.repositories;

import com.etherea.models.PickupPointDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupPointDeliveryRepository  extends JpaRepository<PickupPointDelivery, Long> {
}