package com.source.bundleboard.payment.controller;

import com.source.bundleboard.payment.dto.PaymentRequest;
import com.source.bundleboard.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @MutationMapping
    public Mono<String> createCheckoutSession(@Argument @Valid PaymentRequest input) throws StripeException {
        return paymentService.createCheckoutSession(input);
    }

}
