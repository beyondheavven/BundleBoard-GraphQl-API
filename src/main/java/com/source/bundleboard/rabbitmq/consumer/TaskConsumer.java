package com.source.bundleboard.rabbitmq.consumer;

import com.source.bundleboard.email.mail.service.MailService;
import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.dto.StorageTask;
import com.source.bundleboard.storage.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final MailService mailService;

    private final SupabaseStorageService supabaseStorageService;

    @RabbitListener(queues = "#{@rabbitProperties.emailQueue}")
    public void processEmail(EmailTask emailTask) {
        log.info("RabbitMQ Worker started email task: {}", emailTask.toEmail());
        try {
            mailService.sendTemplateEmailSync(
                    emailTask.toEmail(),
                    emailTask.subject(),
                    emailTask.templateName(),
                    emailTask.templateVariables()
            );
        } catch (Exception e) {
            log.error("Failed to send email to {}", emailTask.toEmail(), e);
            throw e;
        }

    }

    @RabbitListener(queues = "#{@rabbitProperties.mediaQueue}")
    public void processMedia(StorageTask storageTask) {
        log.info("RabbitMQ Worker started storage task: {}", storageTask.storageOperationType());
        try {
            switch (storageTask.storageOperationType()){
                case DELETE_FILES ->
                    supabaseStorageService.deleteFiles(storageTask.targetPath(), storageTask.bucketName()).block();

                case DELETE_FOLDERS ->
                    supabaseStorageService.deleteFolder(storageTask.targetPath(), storageTask.bucketName()).block();

                default -> log.warn("Unknown storage operation type: {}", storageTask.storageOperationType());
            }

            log.info("Storage task completed: {}", storageTask.storageOperationType());
        }catch (Exception e){
            log.error("Failed to process storage task: {}", storageTask.storageOperationType(), e);
            throw e;
        }

    }
}
