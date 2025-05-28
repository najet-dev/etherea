package com.etherea.services;

import com.etherea.dtos.ContactDTO;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.Contact;
import com.etherea.models.User;
import com.etherea.repositories.ContactRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }
    /**
     * Saves a new contact message sent by a user.
     *
     * @param contactDTO the contact message data to be saved
     * @return the saved contact message as a {@link ContactDTO}
     * @throws UserNotFoundException if the user associated with the message does not exist
     */
    public ContactDTO saveContact(ContactDTO contactDTO) {
        User user = userRepository.findByUsername(contactDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Contact contact = new Contact(user, contactDTO.getSubject(), contactDTO.getMessage());
        Contact savedContact = contactRepository.save(contact);

        return ContactDTO.fromEntity(savedContact);
    }
    /**
     * Retrieves all contact messages associated with a specific user.
     *
     * @param userId the ID of the user whose messages are to be retrieved
     * @return a list of {@link ContactDTO} objects representing the user's messages
     * @throws UserNotFoundException if no user is found with the given ID
     */
    public List<ContactDTO> getUserMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<Contact> contacts = contactRepository.findByUserId(userId);

        return contacts.stream()
                .map(ContactDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
