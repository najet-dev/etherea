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
        this.publicHolidays = publicHolidays;
    }

    // Method for calculating delivery date from delivery method and order date
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryMethod deliveryMethod) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé");
        }
        int deliveryDays = deliveryMethod.calculateDeliveryTime();
        return calculateDeliveryDate(startDate, deliveryDays);
    }

    // Calculation of delivery date based on working days and public holidays
    public LocalDate calculateDeliveryDate(LocalDate startDate, int deliveryDays) {
        LocalDate deliveryDate = startDate;
        int addedDays = 0;

        while (addedDays < deliveryDays) {
            deliveryDate = deliveryDate.plusDays(1);

            if (isWorkingDay(deliveryDate)) {
                addedDays++;
            }
        }
        return deliveryDate;
    }

    // Method to check if a date is a working day
    private boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                (publicHolidays == null || !publicHolidays.contains(date));
    }
}
