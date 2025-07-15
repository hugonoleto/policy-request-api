package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivePolicyRequestSearchService {

    private final PolicyRequestRepository policyRequestRepository;

    public PolicyRequest search(UUID id) {
        List<String> excludedStatuses = Arrays.asList("REJECTED", "PAYMENT_REJECTED", "SUBSCRIPTION_REJECTED", "CANCELED");
        return policyRequestRepository.findByIdAndStatusNotIn(id, excludedStatuses).orElseThrow(RuntimeException::new);
    }
}
