package com.etherea.dtos;

import com.etherea.enums.DeliveryOption;
import java.time.LocalDate;

public class HomeStandardDeliveryDTO extends com.etherea.dto.DeliveryMethodDTO {
    public HomeStandardDeliveryDTO() {}
    public HomeStandardDeliveryDTO(Long id, DeliveryOption deliveryOption, LocalDate expectedDeliveryDate, Double cost) {
        super(id, deliveryOption, expectedDeliveryDate, cost);
    }
}
