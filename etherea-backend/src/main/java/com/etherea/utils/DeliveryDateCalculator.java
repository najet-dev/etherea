package com.etherea.utils;

import com.etherea.models.DeliveryType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Component
public class DeliveryDateCalculator {
    private final Set<LocalDate> publicHolidays;

    @Autowired
    public DeliveryDateCalculator(HolidayProvider holidayProvider) {
        this.publicHolidays = (holidayProvider != null) ? holidayProvider.getPublicHolidays() : Collections.emptySet();
    }

    /**
     * Calculates the delivery date considering business days (excluding weekends and public holidays).
     *
     * @param startDate     The start date of the delivery process.
     * @param deliveryType  The delivery type that specifies the number of business days to add.
     * @return The estimated delivery date.
     * @throws NullPointerException if startDate or deliveryType is null.
     */
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryType deliveryType) {
        Objects.requireNonNull(deliveryType, "The delivery type cannot be null.");
        Objects.requireNonNull(startDate, "The start date cannot be null.");

        int remainingDays = deliveryType.getDeliveryDays();
        LocalDate deliveryDate = startDate;

        while (remainingDays > 0) {
            deliveryDate = deliveryDate.plusDays(1);
            if (isBusinessDay(deliveryDate)) {
                remainingDays--;
            }
        }
        return deliveryDate;
    }

    /**
     * Checks if a given date is a business day (excludes Saturdays, Sundays, and public holidays).
     *
     * @param date The date to check.
     * @return true if it is a business day, false otherwise.
     */
    private boolean isBusinessDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                !publicHolidays.contains(date);
    }
}
