package com.etherea.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stockAvailable;
    private String category;

    @OneToMany(mappedBy = "product")
    private List<CommandItem> commandItems;


}
