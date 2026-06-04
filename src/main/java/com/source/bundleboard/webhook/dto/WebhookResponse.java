package com.source.bundleboard.webhook.dto;

public record WebhookResponse(
        String eventId,

        String eventType,

        String staus,

        String message
) {
}
