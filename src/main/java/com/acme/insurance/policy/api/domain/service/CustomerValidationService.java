package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.service.validator.RiskProfileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerValidationService {

    private final List<RiskProfileValidator> validators;

    public boolean validate(PolicyRequest policyRequest, String riskProfile) {
        return validators.stream()
                .filter(v -> v.supports(riskProfile))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Perfil de risco não suportado: " + riskProfile))
                .validate(policyRequest);
    }
}