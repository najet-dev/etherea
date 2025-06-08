package com.etherea.services;

import com.etherea.dtos.UpdateEmailRequestDTO;
import com.etherea.dtos.UpdatePasswordRequestDTO;
import com.etherea.dtos.UserDTO;
import com.etherea.exception.InvalidEmailException;
import com.etherea.exception.UnauthorizedAccessException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.jwt.JwtUtils;
import com.etherea.models.PasswordHistory;
import com.etherea.models.Role;
import com.etherea.models.User;
import com.etherea.models.Volume;
import com.etherea.repositories.PasswordHistoryRepository;
import com.etherea.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public UserService(JwtUtils jwtUtils, PasswordEncoder passwordEncoder, UserRepository userRepository, PasswordHistoryRepository passwordHistoryRepository) {
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of UserDTO objects representing all users.
     */
    public Page<UserDTO> getAllUsers(int page, int size) {
        // Retrieve page of users
        Page<User> usersPage = userRepository.findAll(PageRequest.of(page, size));

        // Convert Page<User> to Page<UserDTO>
        return usersPage.map(UserDTO::fromUser);
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

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id the ID of the user to be deleted
     * @throws UserNotFoundException if no user exists with the given ID
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Updates the email address of a user after verifying their identity via JWT token and validating the email change.
     *
     * @param updateEmailRequestDTO the DTO containing the user's ID, current email, and new email
     * @param token the JWT token of the authenticated user
     * @return true if the email was successfully updated
     * @throws UserNotFoundException if the user does not exist
     * @throws UnauthorizedAccessException if the token does not match the user requesting the change
     * @throws InvalidEmailException if the current email is incorrect or the new email is invalid
     */
    public boolean updateEmail(UpdateEmailRequestDTO updateEmailRequestDTO, String token) {
        // Extract user email from token
        String emailFromToken = jwtUtils.getUserNameFromJwtToken(token);

        // Check if the user exists in the database
        User user = userRepository.findById(updateEmailRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check that the authenticated user corresponds to the user you want to modify
        if (!user.getUsername().equals(emailFromToken)) {
            throw new UnauthorizedAccessException("You cannot modify another user's email");
        }

        // Check if the current email address matches the user's email address
        if (!user.getUsername().equals(updateEmailRequestDTO.getCurrentEmail())) {
            throw new InvalidEmailException("The current email does not match.");
        }

        // Check if the new email is valid
        String newEmail = updateEmailRequestDTO.getNewEmail();
        if (newEmail == null || newEmail.isEmpty() || !newEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidEmailException("The new email is invalid");
        }

        // Update email
        user.setUsername(newEmail);
        userRepository.save(user);
        return true;
    }

    /**
     * Updates the password of a user after validating the old password, new password constraints,
     * and ensuring the new password has not been used before.
     *
     * @param updatePasswordRequestDTO the DTO containing the user's ID, current password, new password, and confirmation
     * @param token the JWT token of the authenticated user
     * @return true if the password was successfully updated
     * @throws UserNotFoundException if the user does not exist
     * @throws UnauthorizedAccessException if the token does not match the user or the old password is incorrect
     * @throws IllegalArgumentException if the new password is invalid or has been used before
     */
    @Transactional
    public boolean updatePassword(UpdatePasswordRequestDTO updatePasswordRequestDTO, String token) {
        String emailFromToken = jwtUtils.getUserNameFromJwtToken(token);

        User user = userRepository.findById(updatePasswordRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getUsername().equals(emailFromToken)) {
            throw new UnauthorizedAccessException("You cannot change another user's password");
        }

        if (!passwordEncoder.matches(updatePasswordRequestDTO.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("The old password is incorrect");
        }

        if (passwordEncoder.matches(updatePasswordRequestDTO.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("The new password must be different from the old one");
        }

        if (!updatePasswordRequestDTO.getNewPassword().equals(updatePasswordRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match");
        }

        if (updatePasswordRequestDTO.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
        }

        if (!updatePasswordRequestDTO.getNewPassword().matches(".*\\d.*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins un chiffre.");
        }

        if (!updatePasswordRequestDTO.getNewPassword().matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une lettre.");
        }

        // Check that the new password has never been used before
        List<PasswordHistory> oldPasswords = passwordHistoryRepository.findByUserId(user.getId());
        for (PasswordHistory oldPassword : oldPasswords) {
            if (passwordEncoder.matches(updatePasswordRequestDTO.getNewPassword(), oldPassword.getHashedPassword())) {
                throw new IllegalArgumentException("Vous ne pouvez pas réutiliser un ancien mot de passe.");
            }
        }

        // Save old password
        passwordHistoryRepository.save(new PasswordHistory(user, user.getPassword()));

        // Update password
        user.setPassword(passwordEncoder.encode(updatePasswordRequestDTO.getNewPassword()));
        userRepository.save(user);

        return true;
    }
}