package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicySearchByIdService {

    private final PolicyRequestRepository repository;

    public PolicyRequest search(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy request not found for ID: " + id));
    }

}
