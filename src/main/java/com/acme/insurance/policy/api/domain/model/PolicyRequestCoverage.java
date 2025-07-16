package com.acme.insurance.policy.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "policy_request_coverage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestCoverage {

    @EmbeddedId
    private PolicyRequestCoverageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("policyRequestId")
    @JoinColumn(name = "policy_request_id")
    private PolicyRequest policyRequest;

    @Column(nullable = false)
    private BigDecimal amount;

}