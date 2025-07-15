package com.acme.insurance.policy.api.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "policy_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequest {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "sales_channel", nullable = false, length = 30)
    private String salesChannel;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(nullable = false, length = 24)
    private String status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "total_monthly_premium_amount", nullable = false)
    private BigDecimal totalMonthlyPremiumAmount;

    @Column(name = "insured_amount", nullable = false)
    private BigDecimal insuredAmount;

    @OneToMany(mappedBy = "policyRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PolicyRequestCoverage> coverages = new HashSet<>();

    @OneToMany(mappedBy = "policyRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PolicyRequestAssistance> assistances = new HashSet<>();

    @OneToMany(mappedBy = "policyRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PolicyRequestHistory> history = new ArrayList<>();

    public void init() {
        this.history = new ArrayList<>();
        this.createdAt = OffsetDateTime.now();
        this.history.add(new PolicyRequestHistory(this.status, this, this.createdAt));
    }

    public void addHistory(String status) {
        OffsetDateTime now = OffsetDateTime.now();
        this.history.add(new PolicyRequestHistory(status, this, now));
        this.status = status;
        if ("FINISHED".equals(status)) {
            this.finishedAt = now;
        }
    }

}