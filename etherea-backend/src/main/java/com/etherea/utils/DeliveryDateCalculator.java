package com.etherea.utils;

import com.etherea.models.DeliveryMethod;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
@Component
public class DeliveryDateCalculator {

    private final Set<LocalDate> publicHolidays;

    // Constructeur pour injecter les jours fériés, rend la classe plus flexible
    public DeliveryDateCalculator(Set<LocalDate> publicHolidays) {
        this.publicHolidays = publicHolidays;
    }

    // Méthode pour calculer la date de livraison à partir de la méthode de livraison et d'une date de commande
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryMethod deliveryMethod) {
        int deliveryDays = deliveryMethod.calculateDeliveryTime(); // Récupère le temps de livraison
        return calculateDeliveryDate(startDate, deliveryDays);
    }

    // Calcul de la date de livraison en fonction de jours ouvrés et fériés
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

    // Méthode pour vérifier si une date est un jour ouvré
    private boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                (publicHolidays == null || !publicHolidays.contains(date));
    }
}
