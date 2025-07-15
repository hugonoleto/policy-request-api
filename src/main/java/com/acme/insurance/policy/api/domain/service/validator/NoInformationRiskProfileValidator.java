package com.acme.insurance.policy.api.domain.service.validator;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import org.springframework.stereotype.Component;

import static com.acme.insurance.policy.api.domain.model.enums.RiskProfile.NO_INFORMATION;

@Component
public class NoInformationRiskProfileValidator implements RiskProfileValidator {

    @Override
    public boolean supports(String riskProfile) {
        return NO_INFORMATION.name().equalsIgnoreCase(riskProfile);
    }

    @Override
    public boolean validate(PolicyRequest policyRequest) {
        return switch (policyRequest.getCategory().toUpperCase()) {
            case "LIFE", "RESIDENTIAL" -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("200000")) <= 0;
            case "AUTO" -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("75000")) <= 0;
            default -> policyRequest.getInsuredAmount().compareTo(new java.math.BigDecimal("55000")) <= 0;
        };
    }
}