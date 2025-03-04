package com.etherea.services;

import com.etherea.dtos.UpdateEmailRequestDTO;
import com.etherea.dtos.UserDTO;
import com.etherea.exception.InvalidEmailException;
import com.etherea.exception.UnauthorizedAccessException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.jwt.JwtUtils;
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
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
    public boolean updateEmail(UpdateEmailRequestDTO updateEmailRequestDTO, String token) {
        // Extraire l'email de l'utilisateur depuis le token
        String emailFromToken = jwtUtils.getUserNameFromJwtToken(token);

        // Vérifier si l'utilisateur existe en base de données
        User user = userRepository.findById(updateEmailRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur authentifié correspond bien à l'utilisateur qu'il veut modifier
        if (!user.getUsername().equals(emailFromToken)) {
            throw new UnauthorizedAccessException("Vous ne pouvez pas modifier l'email d'un autre utilisateur.");
        }

        // Vérifier si l'email actuel correspond à celui de l'utilisateur
        if (!user.getUsername().equals(updateEmailRequestDTO.getCurrentEmail())) {
            throw new InvalidEmailException("L'email actuel ne correspond pas.");
        }

        // Vérifier si le nouvel email est valide
        String newEmail = updateEmailRequestDTO.getNewEmail();
        if (newEmail == null || newEmail.isEmpty() || !newEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidEmailException("Le nouvel email n'est pas valide.");
        }

        // Mettre à jour l'email
        user.setUsername(newEmail);
        userRepository.save(user);
        return true;
    }

}
