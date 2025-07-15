package com.acme.insurance.policy.api.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public <T> void publish(T event, String queueName) {
        rabbitTemplate.convertAndSend(
                "policy.exchange",
                queueName,
                event,
                message -> {
                    message.getMessageProperties().setHeader("__TypeId__", event.getClass().getSimpleName());
                    return message;
                }
        );
    }
}