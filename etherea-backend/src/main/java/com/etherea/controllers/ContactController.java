package com.etherea.controllers;

import com.etherea.dtos.ContactDTO;
import com.etherea.dtos.FavoriteDTO;
import com.etherea.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
@CrossOrigin
public class ContactController {
    @Autowired
    private ContactService contactService;
    @PostMapping("/save-message")
    public ContactDTO createContact(@RequestBody ContactDTO contactDTO) {
        return contactService.saveContact(contactDTO);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserMessages(@PathVariable Long userId) {
        try {
            List<ContactDTO> userMessages = contactService.getUserMessages(userId);
            return ResponseEntity.ok(userMessages);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
