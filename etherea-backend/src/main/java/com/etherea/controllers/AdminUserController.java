package com.etherea.controllers;

import com.etherea.dtos.UserDTO;
import com.etherea.exception.UserNotFoundException;
import com.etherea.payload.response.MessageResponse;
import com.etherea.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminUserController {

    @Autowired
    private UserService userService;


}
