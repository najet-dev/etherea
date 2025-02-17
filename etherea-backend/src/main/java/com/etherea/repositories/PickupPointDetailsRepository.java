package com.etherea.repositories;

import com.etherea.models.PaymentMethod;
import com.etherea.models.PickupPointDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupPointDetailsRepository  extends JpaRepository<PickupPointDetails, Long> {
}
