package com.source.bundleboard.rabbitmq.dto;

import java.util.Map;

public record EmailTask(

        String toEmail,

        String subject,

        String templateName,

        Map<String, Object> templateVariables
) {
}
