package com.etherea.controllers;

import com.etherea.dtos.ContactDTO;
import com.etherea.services.ContactService;
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
    private final ContactService contactService;
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Saves a contact message submitted by the user.
     *
     * @param contactDTO the data transfer object containing contact message details
     * @return the saved ContactDTO
     */
    @PostMapping("/save-message")
    public ContactDTO createContact(@RequestBody ContactDTO contactDTO) {
        return contactService.saveContact(contactDTO);
    }

    /**
     * Retrieves all contact messages associated with a specific user.
     *
     * @param userId the ID of the user whose messages are to be retrieved
     * @return a list of ContactDTOs or an error response if the user is not found
     */
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