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

    /**
     * Retrieves all users from the database.
     *
     * @return A list of UserDTO objects representing all users.
     */
    public List<UserDTO> getAllUsers() {
        // Retrieve all users
        List<User> users = userRepository.findAll();

        // Convert User entities to UserDTO objects
        return users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user.
     * @return A UserDTO representing the user, if found.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromUser)
                .orElseThrow(() -> {
                    logger.error("No user found with ID: {}", id);
                    return new UserNotFoundException("No user found with ID: " + id);
                });
    }
}
