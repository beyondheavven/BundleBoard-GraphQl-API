package com.source.bundleboard.payment.service;

import com.source.bundleboard.payment.dto.PaymentRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceImpl implements PaymentService {


    @Override
    public Mono<String> createCheckoutSession(PaymentRequest input) {
        return null;
    }
}
