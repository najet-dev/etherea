package com.etherea.controllers;

import com.etherea.dtos.CommandResponseDTO;
import com.etherea.enums.CommandStatus;
import com.etherea.services.CommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/command")
@CrossOrigin(origins = "*")
public class CommandController {
    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Récupère toutes les commandes d'un utilisateur.
     *
     * @param userId ID de l'utilisateur.
     * @return Liste des commandes.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommandResponseDTO>> getUserCommands(@PathVariable Long userId) {
        List<CommandResponseDTO> commands = commandService.getCommandsByUserId(userId);
        return ResponseEntity.ok(commands);
    }

    /**
     * Récupère une commande spécifique d'un utilisateur.
     *
     * @param userId    ID de l'utilisateur.
     * @param commandId ID de la commande.
     * @return La commande si trouvée, sinon 404.
     */
    @GetMapping("/user/{userId}/command/{commandId}")
    public ResponseEntity<CommandResponseDTO> getUserCommandById(
            @PathVariable Long userId,
            @PathVariable Long commandId) {

        Optional<CommandResponseDTO> commandDTO = commandService.getCommandByUserIdAndCommandId(userId, commandId);
        return commandDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(null));
    }

    /**
     * Met à jour le statut d'une commande.
     *
     * @param commandId ID de la commande.
     * @param newStatus Nouveau statut.
     * @return Réponse JSON indiquant le succès ou l'échec.
     */
    @PutMapping("/{commandId}/status")
    public ResponseEntity<Map<String, String>> updateCommandStatus(
            @PathVariable Long commandId,
            @RequestParam CommandStatus newStatus) {

        Map<String, String> response = new HashMap<>();
        try {
            commandService.updateCommandStatus(commandId, newStatus);
            response.put("message", "Statut de la commande mis à jour avec succès !");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Erreur lors de la mise à jour du statut : " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Annule une commande.
     *
     * @param commandId ID de la commande.
     * @return Réponse JSON indiquant le succès ou l'échec.
     */
    @PutMapping("/{commandId}/cancel")
    public ResponseEntity<Map<String, String>> cancelCommand(@PathVariable Long commandId) {
        Map<String, String> response = new HashMap<>();
        boolean canceled = commandService.cancelCommand(commandId);

        if (canceled) {
            response.put("message", "Commande annulée avec succès !");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Impossible d'annuler la commande.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
