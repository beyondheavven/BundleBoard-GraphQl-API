package com.source.bundleboard.rabbitmq.dto;

public record StorageTask(

        StorageOperationType storageOperationType,

        String targetPath,

        String bucketName
) {
}
