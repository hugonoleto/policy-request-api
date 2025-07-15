package com.acme.insurance.policy.api.domain.service.validator;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import org.springframework.stereotype.Component;

import static com.acme.insurance.policy.api.domain.model.enums.RiskProfile.HIGH_RISK;

@Component
public class HighRiskProfileValidator implements RiskProfileValidator {

    @Override
    public boolean supports(String riskProfile) {
        return HIGH_RISK.name().equalsIgnoreCase(riskProfile);
    }

    @Override
    public boolean validate(PolicyRequest policyRequest) {
        return switch (policyRequest.getCategory().toUpperCase()) {
            case "AUTO" -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("250000")) <= 0;
            case "RESIDENTIAL" -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("150000")) <= 0;
            default -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("125000")) <= 0;
        };
    }
}
