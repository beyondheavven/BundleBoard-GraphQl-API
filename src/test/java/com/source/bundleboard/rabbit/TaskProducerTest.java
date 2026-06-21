package com.source.bundleboard.rabbit;

import com.source.bundleboard.config.properties.RabbitProperties;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.dto.StorageOperationType;
import com.source.bundleboard.rabbitmq.dto.StorageTask;
import com.source.bundleboard.rabbitmq.dto.WebhookTask;
import com.source.bundleboard.rabbitmq.producer.TaskProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitProperties rabbitProperties;

    @InjectMocks
    private TaskProducerImpl taskProducer;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(rabbitProperties.getEmailQueue()).thenReturn("email_queue");
        org.mockito.Mockito.lenient().when(rabbitProperties.getMediaQueue()).thenReturn("media_queue");
        org.mockito.Mockito.lenient().when(rabbitProperties.getWebhookQueue()).thenReturn("webhook_queue");
    }

    @Test
    void sendEmailTask_Success() {
        EmailTask task = new EmailTask("test@test.com", "Subject", "template", Map.of());

        StepVerifier.create(taskProducer.sendEmailTask(task))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend("email_queue", task);
    }

    @Test
    void sendStorageTask_Success() {
        StorageTask task = new StorageTask(StorageOperationType.DELETE_FILES, "path", "bucket");

        StepVerifier.create(taskProducer.sendStorageTask(task))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend("media_queue", task);
    }

    @Test
    void sendWebhookTask_Success() {
        String json = "{\"id\":\"evt_123\"}";

        StepVerifier.create(taskProducer.sendWebhookTask(json))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend(eq("webhook_queue"), any(WebhookTask.class));
    }
}