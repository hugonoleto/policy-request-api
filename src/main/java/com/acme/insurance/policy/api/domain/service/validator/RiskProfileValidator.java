package com.acme.insurance.policy.api.domain.service.validator;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;

public interface RiskProfileValidator {

    boolean supports(String riskProfile);
    boolean validate(PolicyRequest policyRequest);

}