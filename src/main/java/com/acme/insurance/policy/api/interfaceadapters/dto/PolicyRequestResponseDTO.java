package com.acme.insurance.policy.api.interfaceadapters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestResponseDTO {

    private UUID id;
    private UUID customerId;
    private UUID productId;
    private String category;
    private String salesChannel;
    private String paymentMethod;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime finishedAt;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    private List<PolicyRequestCoverageDTO> coverages;
    private List<PolicyRequestAssistanceDTO> assistances;
    private List<PolicyRequestHistoryDTO> history;

}