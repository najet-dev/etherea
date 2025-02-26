package com.etherea.utils;

import com.etherea.models.DeliveryMethod;
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
     * Calcule la date de livraison en tenant compte des jours ouvrés (hors week-ends et jours fériés).
     *
     * @param startDate Date de début de la livraison
     * @param method    Méthode de livraison contenant le nombre de jours ouvrés à ajouter
     * @return La date estimée de livraison
     */
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryMethod method) {
        Objects.requireNonNull(method, "La méthode de livraison ne peut pas être null.");
        Objects.requireNonNull(startDate, "La date de début ne peut pas être null.");

        int remainingDays = method.getDeliveryDays();
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
     * Vérifie si une date est un jour ouvré (exclut samedi, dimanche et jours fériés).
     *
     * @param date La date à vérifier
     * @return true si c'est un jour ouvré, false sinon
     */
    private boolean isBusinessDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                !publicHolidays.contains(date);
    }
}
