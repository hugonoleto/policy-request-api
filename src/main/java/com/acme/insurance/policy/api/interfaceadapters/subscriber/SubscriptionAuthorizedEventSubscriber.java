package com.acme.insurance.policy.api.interfaceadapters.subscriber;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.event.PaymentSubscriptionStatusEvent;
import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus;
import com.acme.insurance.policy.api.domain.service.ActivePolicyRequestSearchService;
import com.acme.insurance.policy.api.domain.service.CustomerBlockedExistsService;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.acme.insurance.policy.api.domain.model.enums.EntityType.SUBSCRIPTION;
import static com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus.APPROVED;
import static com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.PENDING;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionAuthorizedEventSubscriber {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final CustomerBlockedExistsService customerBlockedExistsService;
    private final GenericEventPublisher publisher;

    private static final String INVALID_SUBSCRIPTION_STATUS_MESSAGE = "Status inválido para processamento de assinatura.";

    @RabbitListener(queues = QueueNames.POLICY_SUBSCRIPTION)
    public void handleSubscriptionAuthorized(GenericEvent event) {
        PolicyRequest policyRequest = activePolicyRequestSearchService.search(event.getPolicyRequestId());
        if (policyRequest.getStatus().equals(PENDING.name())) {
            if (customerBlockedExistsService.exists(policyRequest.getCustomerId(), SUBSCRIPTION)) {
                log.warn("O cliente com ID {} da solicitação {} teve a assinatura rejeitada por bloqueios.",
                        policyRequest.getCustomerId(), policyRequest.getId());
                publisher.publish(buildSubscriptionStatusEvent(policyRequest.getId(), REJECTED), QueueNames.POLICY_STATE_CHANGED);
            } else {
                log.warn("O cliente com ID {} da solicitação {} teve a assinatura aprovada.",
                        policyRequest.getCustomerId(), policyRequest.getId());
                publisher.publish(buildSubscriptionStatusEvent(policyRequest.getId(), APPROVED), QueueNames.POLICY_STATE_CHANGED);
            }
        } else {
            log.error("Não é possivel processar assinatura da solicitação {}, pois está com status diferente de {}",
                    event.getPolicyRequestId(), PENDING.name());
            throw new PolicyBadRequestException(INVALID_SUBSCRIPTION_STATUS_MESSAGE);
        }
    }

    private PaymentSubscriptionStatusEvent buildSubscriptionStatusEvent(UUID policyRequestId, PaymentSubscriptionStatus paymentSubscriptionStatus) {
        return PaymentSubscriptionStatusEvent.builder()
                .policyRequestId(policyRequestId)
                .paymentSubscriptionStatus(paymentSubscriptionStatus)
                .entityType(SUBSCRIPTION)
                .build();
    }
}
