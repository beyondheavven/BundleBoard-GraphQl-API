package com.source.bundleboard.webhook.service;

import com.stripe.model.Event;
import reactor.core.publisher.Mono;

public interface WebhookService {

    Mono<Void> processEvent(Event event);
}
