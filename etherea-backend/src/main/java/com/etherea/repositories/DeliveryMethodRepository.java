package com.etherea.repositories;

import com.etherea.models.DeliveryAddress;
import com.etherea.models.DeliveryMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {

}
