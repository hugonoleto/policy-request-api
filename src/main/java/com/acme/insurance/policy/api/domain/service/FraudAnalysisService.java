package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.FraudAnalysis;

public interface FraudAnalysisService {

    FraudAnalysis getFraudAnalysis(String customerId);

}