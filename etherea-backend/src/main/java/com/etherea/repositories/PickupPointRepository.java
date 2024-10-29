package com.etherea.repositories;

import com.etherea.models.PaymentMethod;
import com.etherea.models.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickupPointRepository extends JpaRepository<PickupPoint, Long>{
    List<PickupPoint> findByLatitudeBetweenAndLongitudeBetween(Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude);

}
