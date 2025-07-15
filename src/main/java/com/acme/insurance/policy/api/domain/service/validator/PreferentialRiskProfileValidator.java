package com.acme.insurance.policy.api.domain.service.validator;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import org.springframework.stereotype.Component;

import static com.acme.insurance.policy.api.domain.model.enums.RiskProfile.PREFERENTIAL;

@Component
public class PreferentialRiskProfileValidator implements RiskProfileValidator {

    @Override
    public boolean supports(String riskProfile) {
        return PREFERENTIAL.name().equalsIgnoreCase(riskProfile);
    }

    @Override
    public boolean validate(PolicyRequest policyRequest) {
        return switch (policyRequest.getCategory().toUpperCase()) {
            case "LIFE" -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("800000")) <= 0;
            case "AUTO", "RESIDENTIAL" -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("450000")) <= 0;
            default -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("375000")) <= 0;
        };
    }
}