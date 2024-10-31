package com.etherea.interfaces;

import java.util.List;
import java.util.Map;
public interface IOverpass {
    List<Map<String, Object>> getNearbyPickupPoints(double latitude, double longitude, double radius);
    String getCompleteAddress(double latitude, double longitude);
}

