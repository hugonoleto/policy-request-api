package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.acme.insurance.policy.api.domain.state.State.CANCELED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelPolicyRequestService {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final PolicyRequestRepository repository;

    public void cancel(UUID id) {
        log.info("Iniciando cancelamento da solicitação com ID: {}", id);
        PolicyRequest policyRequest = activePolicyRequestSearchService.search(id);

        policyRequest.addHistory(CANCELED.name());
        repository.save(policyRequest);
        log.info("Solicitação {} cancelada com sucesso.", policyRequest.getId());
    }

}
