package com.source.bundleboard.payment.controller;

import com.source.bundleboard.payment.dto.PaymentRequest;
import com.source.bundleboard.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @MutationMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'AUTHOR', 'ADMIN')")
    public Mono<String> createCheckoutSession(@Argument @Valid PaymentRequest input) {
        return paymentService.createCheckoutSession(input);
    }

}
