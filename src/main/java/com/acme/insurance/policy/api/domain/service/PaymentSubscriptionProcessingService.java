package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.PaymentSubscriptionStatusEvent;
import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.enums.EntityType;
import com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus.APPROVED;
import static com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSubscriptionProcessingService {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final CustomerBlockedExistsService customerBlockedExistsService;
    private final GenericEventPublisher publisher;

    private static final String INVALID_STATUS_MESSAGE = "Status inválido para processamento de %s.";

    public void process(UUID policyRequestId, EntityType entityType) {
        PolicyRequest policyRequest = activePolicyRequestSearchService.search(policyRequestId);
        if (policyRequest.getStatus().equals(PENDING.name())) {
            if (customerBlockedExistsService.exists(policyRequest.getCustomerId(), entityType)) {
                log.warn("O cliente com ID {} da solicitação {} teve a requisição de {} rejeitado por bloqueios.",
                        policyRequest.getCustomerId(), policyRequest.getId(), entityType.getDescription());
                publisher.publish(buildStatusEvent(policyRequest.getId(), REJECTED, entityType), QueueNames.POLICY_STATE_CHANGED);
            } else {
                log.info("O cliente com ID {} da solicitação {} teve a requisição de {} aprovado.",
                        policyRequest.getCustomerId(), policyRequest.getId(), entityType.getDescription());
                publisher.publish(buildStatusEvent(policyRequest.getId(), APPROVED, entityType), QueueNames.POLICY_STATE_CHANGED);
            }
        } else {
            log.error("Não é possivel processar a requisição de {} da solicitação {}, pois está com status diferente de {}",
                    entityType.getDescription(), policyRequestId, PENDING.name());
            throw new PolicyBadRequestException(String.format(INVALID_STATUS_MESSAGE, entityType.getDescription()));
        }
    }

    private PaymentSubscriptionStatusEvent buildStatusEvent(UUID policyRequestId, PaymentSubscriptionStatus status, EntityType entityType) {
        return PaymentSubscriptionStatusEvent.builder()
                .policyRequestId(policyRequestId)
                .paymentSubscriptionStatus(status)
                .entityType(entityType)
                .build();
    }
}