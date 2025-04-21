package com.etherea.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.etherea.payload.request.SignupRequest;
import com.etherea.payload.response.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;

import com.etherea.enums.ERole;
import com.etherea.models.Role;
import com.etherea.models.User;
import com.etherea.payload.request.LoginRequest;
import com.etherea.payload.response.MessageResponse;
import com.etherea.repositories.RoleRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.jwt.JwtUtils;
import com.etherea.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Endpoint for user authentication.
     *
     * @param loginRequest The login request containing username and password.
     * @return JWT response with user details and token, or an unauthorized status if authentication fails.
     */
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Charger les détails de l'utilisateur depuis le service
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Mettre à jour le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Récupérer les rôles de l'utilisateur
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            roles = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .collect(Collectors.toList());

            // Générer le token JWT
            String jwtToken = jwtUtils.generateJwtToken(userDetails.getUsername(), new HashSet<>(roles));

            // Créer la réponse avec les détails de l'utilisateur et le token JWT
            JwtResponse jwtResponse = new JwtResponse(
                    jwtToken,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles);

            return ResponseEntity.ok().body(jwtResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Endpoint for user registration.
     *
     * @param signUpRequest The request containing user details and roles.
     * @return Response entity indicating success or failure of registration.
     */

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        System.out.println("Raw request payload: " + signUpRequest);
        System.out.println("Roles received in request: " + signUpRequest.getRoles());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<Role> roles = new HashSet<>();

        if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
            for (String role : signUpRequest.getRoles()) {
                Role foundRole = validateAndGetRole(role);
                roles.add(foundRole);
            }

            // If the user has ROLE_ADMIN, we also add ROLE_USER
            if (roles.stream().anyMatch(r -> r.getName().equals(ERole.ROLE_ADMIN))) {
                roles.add(validateAndGetRole("USER"));
            }
        } else {
            // No role specified -> default to ROLE_USER
            System.out.println("Aucun rôle spécifié, attribution automatique de ROLE_USER.");
            roles.add(validateAndGetRole("USER"));
        }

        user.setRoles(roles);
        userRepository.save(user);

        System.out.println("User saved with roles: " + user.getRoles());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    private Role validateAndGetRole(String role) {
        String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
        return roleRepository.findByName(ERole.valueOf(formattedRole))
                .orElseThrow(() -> new RuntimeException("Error: Role " + formattedRole + " not found."));
    }

    /**
     * Endpoint for user logout.
     *
     * @param request The HTTP request.
     * @return Response entity indicating success or failure of logout.
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        // Check if the user is authenticated before logging out
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
            securityContextLogoutHandler.logout(request, null, null);
            return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("User is not logged in!"));
        }
    }
}