package com.etherea.interfaces;

import java.util.Map;

public interface IGeocoding {
    Map<String, Double> getCoordinates(String address);
}
