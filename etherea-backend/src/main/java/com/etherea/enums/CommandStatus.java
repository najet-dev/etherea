package com.etherea.enums;

public enum CommandStatus {
    PENDING,         // Commande créée, en attente de paiement
    PAID,            // Paiement confirmé
    PROCESSING,      // Commande en cours de préparation
    SHIPPED,         // Commande expédiée
    DELIVERED,       // Commande livrée
    CANCELLED        // Commande annulée
}
