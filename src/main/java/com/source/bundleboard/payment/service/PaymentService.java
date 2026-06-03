package com.source.bundleboard.payment.service;

import com.source.bundleboard.payment.dto.PaymentRequest;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public interface PaymentService {


    Mono<String> createCheckoutSession(@Valid PaymentRequest input);
}
