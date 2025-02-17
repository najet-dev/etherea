package com.etherea.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class HolidayProvider {

    /**
     * Récupère les jours fériés de l'année en cours.
     */
    public Set<LocalDate> getPublicHolidays() {
        int year = LocalDate.now().getYear();
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(year, 1, 1));  // Jour de l'An
        holidays.add(LocalDate.of(year, 5, 1));  // Fête du Travail
        holidays.add(LocalDate.of(year, 5, 8));  // Victoire 1945
        holidays.add(LocalDate.of(year, 7, 14)); // Fête Nationale
        holidays.add(LocalDate.of(year, 8, 15)); // Assomption
        holidays.add(LocalDate.of(year, 11, 1)); // Toussaint
        holidays.add(LocalDate.of(year, 11, 11));// Armistice 1918
        holidays.add(LocalDate.of(year, 12, 25));// Noël

        return holidays;
    }
}
