package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePolicyRequestService {

    private final PolicyRequestRepository repository;
    private final GenericEventPublisher publisher;

    public PolicyRequest process(PolicyRequest entity) {
        repository.save(entity);
        publisher.publish(new GenericEvent(entity.getId()), QueueNames.POLICY_STATE_CHANGED);
        return entity;
    }

}