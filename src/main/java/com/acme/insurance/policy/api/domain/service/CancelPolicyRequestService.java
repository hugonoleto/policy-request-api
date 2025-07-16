package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.acme.insurance.policy.api.domain.state.State.CANCELED;

@Service
@RequiredArgsConstructor
public class CancelPolicyRequestService {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final PolicyRequestRepository repository;


    public void cancel(UUID id) {
        PolicyRequest enitity = activePolicyRequestSearchService.search(id);

        enitity.addHistory(CANCELED.name());
        repository.save(enitity);
    }

}
