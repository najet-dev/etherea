package com.etherea.utils;

import com.etherea.models.DeliveryMethod;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/**
 * Calculateur de dates de livraison en tenant compte des jours ouvrés et des jours fériés.
 */
@Component
public class DeliveryDateCalculator {

    private final Set<LocalDate> publicHolidays;

    public DeliveryDateCalculator(Set<LocalDate> publicHolidays) {
        this.publicHolidays = publicHolidays;
    }

    /**
     * Calcule la date de livraison en fonction de la méthode et de la date de début.
     *
     * @param startDate      La date de début.
     * @param deliveryMethod La méthode de livraison.
     * @return La date estimée de livraison.
     */
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryMethod deliveryMethod) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé.");
        }
        return calculateDeliveryDate(startDate, deliveryMethod.calculateDeliveryTime());
    }

    /**
     * Calcule la date de livraison en fonction du nombre de jours de livraison.
     *
     * @param startDate    La date de début.
     * @param deliveryDays Le nombre de jours de livraison.
     * @return La date estimée de livraison.
     */
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

    /**
     * Vérifie si une date est un jour ouvré.
     *
     * @param date La date à vérifier.
     * @return true si c'est un jour ouvré, sinon false.
     */
    private boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                (publicHolidays == null || !publicHolidays.contains(date));
    }
}
