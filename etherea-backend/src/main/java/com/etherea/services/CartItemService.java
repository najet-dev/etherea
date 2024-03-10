package com.etherea.services;

import com.etherea.dtos.CartItemDTO;
import com.etherea.dtos.ProductDTO;
import com.etherea.models.CartItem;
import com.etherea.models.Product;
import com.etherea.repositories.CartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

}
