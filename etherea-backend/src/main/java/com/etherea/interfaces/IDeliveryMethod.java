package com.etherea.interfaces;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
public interface IDeliveryMethod {
    DeliveryMethodDTO calculateDeliveryMethod(DeliveryOption option, Long userId, double radius);
}
