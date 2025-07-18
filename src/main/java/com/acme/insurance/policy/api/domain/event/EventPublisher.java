package com.acme.insurance.policy.api.domain.event;

public interface EventPublisher {

    void publish(GenericEvent event, String queueName);

}
