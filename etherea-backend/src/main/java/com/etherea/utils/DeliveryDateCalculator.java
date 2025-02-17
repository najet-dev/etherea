package com.etherea.utils;

import com.etherea.models.DeliveryMethod;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Component
public class DeliveryDateCalculator {
    private final Set<LocalDate> publicHolidays;

    public DeliveryDateCalculator(HolidayProvider holidayProvider) {
        this.publicHolidays = holidayProvider != null ? holidayProvider.getPublicHolidays() : Collections.emptySet();
    }
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("La méthode de livraison ne peut pas être null.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("La date de début ne peut pas être null.");
        }

        LocalDate deliveryDate = startDate;
        int addedDays = 0;

        while (addedDays < method.getDeliveryDays()) {
            deliveryDate = deliveryDate.plusDays(1);
            if (isBusinessDay(deliveryDate)) {
                addedDays++;
            }
        }
        return deliveryDate;
    }
    private boolean isBusinessDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                !publicHolidays.contains(date);
    }
}
