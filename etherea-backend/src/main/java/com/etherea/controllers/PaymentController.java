package com.etherea.controllers;

import com.etherea.dtos.PaymentRequestDTO;
import com.etherea.dtos.PaymentResponse;
import com.etherea.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public PaymentResponse processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        return paymentService.processPayment(paymentRequestDTO);
    }
}
