package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import java.time.LocalDate;

public class HomeExpressDeliveryDTO extends com.etherea.dto.DeliveryMethodDTO {

    public HomeExpressDeliveryDTO() {}

    public HomeExpressDeliveryDTO(Long id, DeliveryOption deliveryOption, LocalDate expectedDeliveryDate, Double cost) {
        super(id, deliveryOption, expectedDeliveryDate, cost);
    }
}
