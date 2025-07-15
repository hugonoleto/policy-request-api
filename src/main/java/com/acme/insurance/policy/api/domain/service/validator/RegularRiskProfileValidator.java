package com.acme.insurance.policy.api.domain.service.validator;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.acme.insurance.policy.api.domain.model.enums.RiskProfile.REGULAR;

@Component
public class RegularRiskProfileValidator implements RiskProfileValidator {

    @Override
    public boolean supports(String riskProfile) {
        return REGULAR.name().equalsIgnoreCase(riskProfile);
    }

    @Override
    public boolean validate(PolicyRequest policyRequest) {
        return switch (policyRequest.getCategory().toUpperCase()) {
            case "LIFE", "RESIDENTIAL" -> policyRequest.getInsuredAmount().compareTo(new BigDecimal("500000")) <= 0;
            case "AUTO" -> policyRequest.getInsuredAmount().compareTo(new BigDecimal("350000")) <= 0;
            default -> policyRequest.getInsuredAmount().compareTo(new BigDecimal("255000")) <= 0;
        };
    }
}
