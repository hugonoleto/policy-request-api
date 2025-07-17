package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.exception.PolicyNotFoundException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicySearchByIdService {

    private final PolicyRequestRepository repository;

    private static final String ERROR_MESSAGE = "A solicitação de apólice informada não existe.";

    public PolicyRequest search(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(ERROR_MESSAGE));
    }

}
