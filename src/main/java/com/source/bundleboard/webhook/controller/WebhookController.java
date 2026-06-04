package com.source.bundleboard.webhook.controller;


import com.source.bundleboard.config.properties.StripeProperties;
import com.source.bundleboard.webhook.service.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

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
                .onErrorMap(SignatureVerificationException.class, ex ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature")
                )
                .flatMap(webhookService::processEvent)
                .onErrorMap(ex -> !(ex instanceof ResponseStatusException), ex ->
                        new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Webhook processing failed")
                );
    }
}
