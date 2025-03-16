package com.etherea.services;

import com.etherea.dtos.ContactDTO;
import com.etherea.dtos.FavoriteDTO;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.Contact;
import com.etherea.models.Favorite;
import com.etherea.models.User;
import com.etherea.repositories.ContactRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;
    public ContactDTO saveContact(ContactDTO contactDTO) {
        User user = userRepository.findByUsername(contactDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Contact contact = new Contact(user, contactDTO.getSubject(), contactDTO.getMessage());
        Contact savedContact = contactRepository.save(contact);

        return ContactDTO.fromEntity(savedContact);
    }
    public List<ContactDTO> getUserMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<Contact> contacts = contactRepository.findByUserId(userId);

        return contacts.stream()
                .map(ContactDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
