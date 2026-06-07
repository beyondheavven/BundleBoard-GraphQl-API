package com.source.bundleboard.config;

import com.source.bundleboard.config.properties.RabbitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitProperties rabbitProperties;

    @Bean
    public Queue mediaQueue() {
        return new Queue(rabbitProperties.getMediaQueue(), true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(rabbitProperties.getEmailQueue(), true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }


}
