package com.etherea.controllers;

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
