package com.source.bundleboard.webhook.controller;


import com.source.bundleboard.config.properties.StripeProperties;
import com.source.bundleboard.rabbitmq.producer.TaskProducer;
import com.source.bundleboard.webhook.service.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final StripeProperties stripeProperties;

    private final TaskProducer taskProducer;

    @PostMapping("/stripe")
    public Mono<ResponseEntity<String>> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        return Mono.fromCallable(() ->
                        Webhook.constructEvent(payload, sigHeader, stripeProperties.getWebhookSecret())
                )
                .flatMap(event -> {
                    return taskProducer.sendWebhookTask(event.toJson());
                })
                .thenReturn(ResponseEntity.ok("Webhook received and queued"))
                .onErrorResume(e -> {
                    log.error("Error in webhook signature", e);
                    return Mono.just(ResponseEntity.badRequest().body("Signature verification failed"));
                });
    }
}
