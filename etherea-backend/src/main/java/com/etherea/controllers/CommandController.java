package com.etherea.controllers;

import com.etherea.dtos.CommandItemDTO;
import com.etherea.dtos.CommandResponseDTO;
import com.etherea.enums.CommandStatus;
import com.etherea.services.CommandService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
     * Retrieves a paginated list of all commands.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of items per page (default is 10)
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link CommandResponseDTO}
     *         if commands are found, or a 204 No Content status if the list is empty
     */@GetMapping
    public ResponseEntity<Page<CommandResponseDTO>> getAllCommands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CommandResponseDTO> commandsPage = commandService.getAllCommands(page, size);

        if (commandsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(commandsPage);
    }

    /**
     * Retrieves all commands from a user.
     *
     * @param userId User ID.
     * @return List of commands.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommandResponseDTO>> getUserCommands(@PathVariable Long userId) {
        List<CommandResponseDTO> commands = commandService.getCommandsByUserId(userId);
        return ResponseEntity.ok(commands);
    }

    /**
     * Retrieves a specific command from a user.
     *
     * @param userId ID of the user.
     * @param commandId Command ID.
     * @return Command if found, otherwise 404.
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
     * Retrieves all items associated with a specific command.
     *
     * @param commandId the ID of the command for which to retrieve items
     * @return a {@link ResponseEntity} containing a list of {@link CommandItemDTO}
     *         representing the items of the specified command
     */
    @GetMapping("/{commandId}/items")
    public ResponseEntity<List<CommandItemDTO>> getCommandItems(@PathVariable Long commandId) {
        List<CommandItemDTO> commandItems = commandService.getCommandItems(commandId);
        return ResponseEntity.ok(commandItems);
    }

    /**
     * Updates order status.
     *
     * @param commandId Order ID.
     * @param newStatus New status.
     * @return JSON response indicating success or failure.
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
     * Cancels an order.
     *
     * @param commandId Command ID.
     * @return JSON response indicating success or failure.
     */
    @PutMapping("/{commandId}/cancel")
    public ResponseEntity<Map<String, String>> cancelCommand(@PathVariable Long commandId) {
        Map<String, String> response = new HashMap<>();
        boolean canceled = commandService.cancelCommand(commandId);

        if (canceled) {
            response.put("message", "Order successfully cancelled !");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Unable to cancel order.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}