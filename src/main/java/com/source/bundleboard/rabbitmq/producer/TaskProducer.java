package com.source.bundleboard.rabbitmq.producer;

import com.source.bundleboard.rabbitmq.dto.EmailTask;
import com.source.bundleboard.rabbitmq.dto.StorageTask;
import reactor.core.publisher.Mono;

public interface TaskProducer {

    Mono<Void> sendEmailTask(EmailTask task);

    Mono<Void> sendStorageTask(StorageTask storageTask);
}
