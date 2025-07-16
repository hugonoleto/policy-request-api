package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestFilter;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyRequestSearchByFiltersService {

    private final PolicyRequestRepository repository;

    public List<PolicyRequest> search(PolicyRequestFilter filter) {
        Specification<PolicyRequest> spec = PolicyRequestSpecifications.filter(filter);
        return repository.findAll(spec);
    }
}