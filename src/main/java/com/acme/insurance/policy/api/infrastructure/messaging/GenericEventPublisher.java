package com.acme.insurance.policy.api.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String POLICY_EXCHANGE = "policy.exchange";

    public <T> void publish(T event, String queueName) {
        rabbitTemplate.convertAndSend(
                POLICY_EXCHANGE,
                queueName,
                event
        );
    }
}