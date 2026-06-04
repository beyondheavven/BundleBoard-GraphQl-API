package com.source.bundleboard.webhook.controller;


import com.source.bundleboard.config.properties.StripeProperties;
import com.source.bundleboard.webhook.service.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    private final StripeProperties stripeProperties;

    @PostMapping("/stripe")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        return Mono.fromCallable(() ->
                        Webhook.constructEvent(payload, sigHeader, stripeProperties.getWebhookSecret())
                )
                .onErrorMap(SignatureVerificationException.class, ex -> {
                    log.error("Stripe signature verification error: {}", ex.getMessage());
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature");
                })
                .flatMap(event -> {
                    log.info("Stripe Webhook received: id={}, type={}", event.getId(), event.getType());
                    return webhookService.processEvent(event);
                })
                .onErrorMap(ex -> !(ex instanceof ResponseStatusException), ex -> {
                    log.error("Internal error whilst processing the webhook", ex);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Webhook processing failed");
                })
                .then();
    }
}
