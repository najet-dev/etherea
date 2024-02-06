package com.etherea.models;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;

import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String password;
    private String phoneNumber;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Command> commands;
}
