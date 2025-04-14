package com.etherea.repositories;

import com.etherea.models.DeliveryMethod;
import com.etherea.models.DeliveryType;
import com.etherea.models.PickupPointDetails;
import com.etherea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    List<DeliveryMethod> findByUserId(Long userId);
}
