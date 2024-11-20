package com.etherea.utils;

import com.etherea.enums.DeliveryOption;
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

    // Méthode pour calculer la date de livraison en fonction de l'option de livraison et de la date de commande
    public LocalDate calculateDeliveryDate(LocalDate startDate, DeliveryOption deliveryOption) {
        // Vérification que la date de début n'est pas dans le passé
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé");
        }

        // Définition des délais de livraison en jours en fonction de l'option de livraison
        int deliveryDays;

        switch (deliveryOption) {
            case HOME_STANDARD:
                deliveryDays = 5; // Délai pour livraison standard
                break;
            case HOME_EXPRESS:
                deliveryDays = 2; // Délai pour livraison express
                break;
            case PICKUP_POINT:
                deliveryDays = 3; // Délai pour livraison en point relais
                break;
            default:
                throw new IllegalArgumentException("Option de livraison inconnue : " + deliveryOption);
        }

        // Appel de la méthode qui calcule la date de livraison en fonction des jours ouvrables
        return calculateDeliveryDate(startDate, deliveryDays);
    }

    // Méthode pour calculer la date de livraison en fonction des jours ouvrables
    public LocalDate calculateDeliveryDate(LocalDate startDate, int deliveryDays) {
        LocalDate deliveryDate = startDate;
        int addedDays = 0;

        // Calcul de la date de livraison en fonction des jours ouvrables
        while (addedDays < deliveryDays) {
            deliveryDate = deliveryDate.plusDays(1);

            if (isWorkingDay(deliveryDate)) {
                addedDays++;
            }
        }
        return deliveryDate;
    }

    // Vérification si une date est un jour ouvré
    private boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                (publicHolidays == null || !publicHolidays.contains(date));
    }
}
