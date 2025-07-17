package com.acme.insurance.policy.api.interfaceadapters.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestCreateDTO {

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID productId;

    @NotNull
    private String category;

    @NotNull
    private String salesChannel;

    @NotNull
    private String paymentMethod;

    @Min(1)
    private BigDecimal totalMonthlyPremiumAmount;

    @Min(1)
    private BigDecimal insuredAmount;

    @NotEmpty
    private Map<String, BigDecimal> coverages;

    @NotEmpty
    private List<String> assistances;

}