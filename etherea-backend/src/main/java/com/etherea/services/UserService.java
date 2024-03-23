package com.etherea.services;

import com.etherea.dtos.UserDTO;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.User;
import com.etherea.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<UserDTO> getAllUsers() {
        // Récupérer tous les utilisateurs
        List<User> users = userRepository.findAll();

        // Convertir les entités User en DTOs UserDTO
        return users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromUser)
                .orElseThrow(() -> {
                    logger.error("No user found with ID: {}", id);
                    return new UserNotFoundException("No user found with ID: " + id);
                });
    }
}
