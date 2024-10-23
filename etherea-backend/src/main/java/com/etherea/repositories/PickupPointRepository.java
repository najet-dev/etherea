package com.etherea.repositories;

import com.etherea.models.PaymentMethod;
import com.etherea.models.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupPointRepository extends JpaRepository<PickupPoint, Long>{
}
