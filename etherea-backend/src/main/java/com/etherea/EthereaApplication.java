package com.etherea;

import com.etherea.dtos.ProductDTO;
import com.etherea.models.Product;
import com.etherea.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class EthereaApplication {
	public static void main(String[] args) {
		SpringApplication.run(EthereaApplication.class, args);
	}

}