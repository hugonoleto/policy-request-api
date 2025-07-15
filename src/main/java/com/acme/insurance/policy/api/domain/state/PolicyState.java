package com.acme.insurance.policy.api.domain.state;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;

public interface PolicyState {

    State current();

    void execute(PolicyRequest policyRequest);

}