package com.source.bundleboard.rabbit;

import com.source.bundleboard.email.mail.service.MailService;
import com.source.bundleboard.rabbitmq.consumer.TaskConsumer;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.dto.StorageOperationType;
import com.source.bundleboard.rabbitmq.dto.StorageTask;
import com.source.bundleboard.rabbitmq.dto.WebhookTask;
import com.source.bundleboard.storage.SupabaseStorageService;
import com.source.bundleboard.webhook.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskConsumerTest {

    @Mock
    private MailService mailService;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private TaskConsumer taskConsumer;

    @Test
    void processEmail_Success() {
        EmailTask task = new EmailTask("test@test.com", "Subject", "template", Map.of());

        taskConsumer.processEmail(task);

        verify(mailService).sendTemplateEmailSync("test@test.com", "Subject", "template", Map.of());
    }

    @Test
    void processEmail_ThrowsException() {
        EmailTask task = new EmailTask("test@test.com", "Subject", "template", Map.of());

        doThrow(new RuntimeException("SMTP Error")).when(mailService)
                .sendTemplateEmailSync(anyString(), anyString(), anyString(), anyMap());

        assertThrows(RuntimeException.class, () -> taskConsumer.processEmail(task));
    }

    @Test
    void processMedia_DeleteFiles_Success() {
        StorageTask task = new StorageTask(StorageOperationType.DELETE_FILES, "path/file.jpg", "bucket");

        when(supabaseStorageService.deleteFiles("path/file.jpg", "bucket")).thenReturn(Mono.empty());

        taskConsumer.processMedia(task);

        verify(supabaseStorageService).deleteFiles("path/file.jpg", "bucket");
    }

    @Test
    void processMedia_DeleteFolders_Success() {
        StorageTask task = new StorageTask(StorageOperationType.DELETE_FOLDERS, "folderPath", "bucket");

        when(supabaseStorageService.deleteFolder("folderPath", "bucket")).thenReturn(Mono.empty());

        taskConsumer.processMedia(task);

        verify(supabaseStorageService).deleteFolder("folderPath", "bucket");
    }

    @Test
    void processMedia_ThrowsException() {
        StorageTask task = new StorageTask(StorageOperationType.DELETE_FILES, "path/file.jpg", "bucket");

        when(supabaseStorageService.deleteFiles(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Storage offline")));

        assertThrows(RuntimeException.class, () -> taskConsumer.processMedia(task));
    }

    @Test
    void processWebhook_Success() {
        String jsonEvent = "{\"id\": \"evt_123\", \"object\": \"event\"}";
        WebhookTask task = new WebhookTask(jsonEvent);

        when(webhookService.processEvent(any())).thenReturn(Mono.empty());

        taskConsumer.processWebhook(task);

        verify(webhookService).processEvent(any());
    }

    @Test
    void processWebhook_InvalidJson_ThrowsException() {
        String invalidJson = "Not a json string";
        WebhookTask task = new WebhookTask(invalidJson);
        assertThrows(Exception.class, () -> taskConsumer.processWebhook(task));
    }
}