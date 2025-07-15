package com.acme.insurance.policy.api.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class FraudAnalysis {

    private String orderId;
    private String customerId;
    private String analyzedAt;
    private String classification;
    private List<Occurrence> occurrences;

}
