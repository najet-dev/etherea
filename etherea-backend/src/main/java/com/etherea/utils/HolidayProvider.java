package com.etherea.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class HolidayProvider {
    public Set<LocalDate> getPublicHolidays() {
        return Set.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 12, 25)
        );
    }
}

