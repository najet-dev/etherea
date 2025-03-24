package com.etherea.dtos;

import com.etherea.models.Role;
import com.etherea.models.User;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private Set<String> roles = new HashSet<>();
    public UserDTO() {
    }
    public UserDTO(Long id, String firstName, String lastName, String username, Set<String> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Conversion de User à UserDTO
    public static UserDTO fromUser(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), roles);
    }

    // Convert UserDTO to User
    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setUsername(this.username);
        return user;
    }
}
