package com.acme.insurance.policy.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class PolicyRequestFilter {

    private UUID customerId;
    private UUID productId;
    private String category;
    private String status;
    private String salesChannel;

}