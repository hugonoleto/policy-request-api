package com.acme.insurance.policy.api.infrastructure.client;

import com.acme.insurance.policy.api.domain.model.FraudAnalysis;
import com.acme.insurance.policy.api.domain.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FraudAnalysisServiceImpl implements FraudAnalysisService {

    private final FraudApiClient fraudApiClient;

    @Override
    public FraudAnalysis getFraudAnalysis(String customerId) {
        return fraudApiClient.getFraudAnalysis(customerId);
    }
}