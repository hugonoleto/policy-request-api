package com.acme.insurance.policy.api.infrastructure.client;

import com.acme.insurance.policy.api.domain.model.FraudAnalysis;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fraudApiClient", url = "${fraud.api.url}")
public interface FraudApiClient {

    @GetMapping("/fraud-analysis/{customerId}")
    FraudAnalysis getFraudAnalysis(@PathVariable("customerId") String customerId);
}