package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.service.validator.RiskProfileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerRiskProfileValidationService {

    private final List<RiskProfileValidator> validators;

    public boolean validate(PolicyRequest policyRequest, String riskProfile) {
        log.info("Iniciando validação de perfil de risco para solicitação ID: {}, Perfil de Risco: {}, Valor Segurado: {}",
                policyRequest.getId(), riskProfile, policyRequest.getInsuredAmount());

        return validators.stream()
                .filter(v -> v.supports(riskProfile))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("A solicitação com ID {} possui um Perfil de Risco inválido: {}. Valor Segurado: {}",
                            policyRequest.getId(), riskProfile, policyRequest.getInsuredAmount());
                    return new PolicyBadRequestException("Perfil de risco não suportado: " + riskProfile);
                })
                .validate(policyRequest);
    }
}