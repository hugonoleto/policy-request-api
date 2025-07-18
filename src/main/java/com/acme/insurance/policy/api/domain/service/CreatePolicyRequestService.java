package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.EventPublisher;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePolicyRequestService {

    private final PolicyRequestRepository repository;
    private final EventPublisher publisher;

    public PolicyRequest create(PolicyRequest policyRequest) {
        log.info("Iniciando criação da solicitação com ID: {}", policyRequest.getId());
        repository.save(policyRequest);
        publisher.publish(new GenericEvent(policyRequest.getId()), QueueNames.POLICY_STATE_CHANGED);
        log.info("Solicitação {} foi enviado para iniciar o fluxo de processamento.", policyRequest.getId());
        return policyRequest;
    }

}