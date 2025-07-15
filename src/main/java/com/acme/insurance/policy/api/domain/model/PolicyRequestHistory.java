package com.acme.insurance.policy.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "policy_request_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_request_id", nullable = false)
    private PolicyRequest policyRequest;

    @Column(nullable = false, length = 24)
    private String status;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    public PolicyRequestHistory(String status, PolicyRequest policyRequest, OffsetDateTime timestamp) {
        this.status = status;
        this.policyRequest = policyRequest;
        this.timestamp = timestamp;
    }
}