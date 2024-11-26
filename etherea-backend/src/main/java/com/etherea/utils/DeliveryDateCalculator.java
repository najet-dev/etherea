package com.etherea.utils;

import com.etherea.models.DeliveryMethod;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DeliveryDateCalculator {
    private final Set<LocalDate> publicHolidays;
    public DeliveryDateCalculator(Set<LocalDate> publicHolidays) {
        if (publicHolidays == null) {
            throw new IllegalArgumentException("Public holidays set cannot be null");
        }
        this.publicHolidays = publicHolidays;
    }
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryMethod deliveryMethod) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé.");
        }
        return calculateDeliveryDate(startDate, deliveryMethod.calculateDeliveryTime());
    }
    public LocalDate calculateDeliveryDate(LocalDate startDate, int deliveryDays) {
        LocalDate deliveryDate = startDate;
        int addedDays = 0;

        while (addedDays < deliveryDays) {
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
