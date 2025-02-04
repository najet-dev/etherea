package com.etherea.controllers;

import com.etherea.enums.CommandStatus;
import com.etherea.services.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/command")
@CrossOrigin
public class CommandController {
    @Autowired
    CommandService commandService;
    @PutMapping("/{commandId}/status")
    public ResponseEntity<String> updateCommandStatus(
            @PathVariable Long commandId,
            @RequestParam CommandStatus newStatus) {

        commandService.updateCommandStatus(commandId, newStatus);
        return ResponseEntity.ok("Statut de la commande mis à jour avec succès !");
    }
    @PostMapping("/{commandId}/cancel")
    public ResponseEntity<String> cancelCommand(@PathVariable Long commandId) {
        boolean canceled = commandService.cancelCommand(commandId);
        if (canceled) {
            return ResponseEntity.ok("Order cancelled successfully");
        } else {
            return ResponseEntity.badRequest().body("Unable to cancel order");
        }
    }
}
