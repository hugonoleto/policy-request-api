package com.acme.insurance.policy.api.domain.state;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.acme.insurance.policy.api.domain.state.State.VALIDATED;

@Component
@RequiredArgsConstructor
public class ValidatedState implements PolicyState {

    private final PolicyRequestRepository repository;
    private final GenericEventPublisher publisher;

    @Override
    public State current() {
        return VALIDATED;
    }

    @Override
    public void execute(PolicyRequest entity) {
        entity.addHistory(State.PENDING.name());
        repository.save(entity);
        publisher.publish(new GenericEvent(entity.getId()), QueueNames.POLICY_PAYMENT);
        publisher.publish(new GenericEvent(entity.getId()), QueueNames.POLICY_SUBSCRIPTION);
    }
}
