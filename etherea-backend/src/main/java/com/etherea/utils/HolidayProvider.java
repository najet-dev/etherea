package com.etherea.utils;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class HolidayProvider {

    /**
     * Retrieves the public holidays for the current year.
     *
     * @return A set of public holidays for the year.
     */
    public Set<LocalDate> getPublicHolidays() {
        int year = LocalDate.now().getYear();
        Set<LocalDate> holidays = new HashSet<>();

        holidays.add(LocalDate.of(year, 1, 1));  // New Year's Day
        holidays.add(LocalDate.of(year, 5, 1));  // Labor Day
        holidays.add(LocalDate.of(year, 5, 8));  // Victory in Europe Day (WWII)
        holidays.add(LocalDate.of(year, 7, 14)); // Bastille Day
        holidays.add(LocalDate.of(year, 8, 15)); // Assumption Day
        holidays.add(LocalDate.of(year, 11, 1)); // All Saints' Day
        holidays.add(LocalDate.of(year, 11, 11));// Armistice Day (WWI)
        holidays.add(LocalDate.of(year, 12, 25));// Christmas Day

        return holidays;
    }
}
