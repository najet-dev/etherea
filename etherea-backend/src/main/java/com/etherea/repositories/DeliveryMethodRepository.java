package com.etherea.repositories;

import com.etherea.models.DeliveryMethod;
import com.etherea.enums.DeliveryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    Optional<DeliveryMethod> findByType(DeliveryType type);
}
