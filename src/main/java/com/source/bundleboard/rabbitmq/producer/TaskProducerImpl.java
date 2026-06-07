package com.source.bundleboard.rabbitmq.producer;

import com.source.bundleboard.config.properties.RabbitProperties;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.dto.StorageTask;
import com.source.bundleboard.rabbitmq.dto.WebhookTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskProducerImpl implements TaskProducer {

    private final RabbitTemplate template;

    private final RabbitProperties rabbitProperties;

    @Override
    public Mono<Void> sendEmailTask(EmailTask task) {
        return Mono.fromRunnable(() -> {
            log.info("Sending email task (RabbitMQ) to: {}", task.toEmail());
            template.convertAndSend(rabbitProperties.getEmailQueue(), task);
        });
    }

    @Override
    public Mono<Void> sendStorageTask(StorageTask storageTask) {
        return Mono.fromRunnable(() -> {
            log.info("Sending storage task (RabbitMQ) to: {}", storageTask.storageOperationType());
            template.convertAndSend(rabbitProperties.getMediaQueue(), storageTask);
        });
    }

    @Override
    public Mono<Void> sendWebhookTask(String eventJson) {
        return Mono.fromRunnable(() -> {
            log.info("Sending Stripe webhook task (RabbitMQ)");
            WebhookTask task = new WebhookTask(eventJson);
            template.convertAndSend(rabbitProperties.getWebhookQueue(), task);
        });
    }
}
