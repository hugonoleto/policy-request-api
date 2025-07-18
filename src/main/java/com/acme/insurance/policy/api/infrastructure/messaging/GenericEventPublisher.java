package com.acme.insurance.policy.api.infrastructure.messaging;

import com.acme.insurance.policy.api.domain.event.EventPublisher;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenericEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String POLICY_EXCHANGE = "policy.exchange";

    @Override
    public void publish(GenericEvent event, String queueName) {
        rabbitTemplate.convertAndSend(
                POLICY_EXCHANGE,
                queueName,
                event
        );
    }
}