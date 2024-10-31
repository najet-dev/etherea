package com.etherea.interfaces;

import com.etherea.dtos.PickupPointDeliveryDTO;

import java.util.List;
public interface IPickupPoint {
    List<PickupPointDeliveryDTO> findNearestPickupPoints(Long userId, double radius);
}
