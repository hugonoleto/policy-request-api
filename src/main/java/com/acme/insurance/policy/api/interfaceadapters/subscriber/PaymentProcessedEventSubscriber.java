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

import static com.acme.insurance.policy.api.domain.model.enums.EntityType.PAYMENT;
import static com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus.APPROVED;
import static com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.PENDING;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProcessedEventSubscriber {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final CustomerBlockedExistsService customerBlockedExistsService;
    private final GenericEventPublisher publisher;

    private static final String INVALID_PAYMENT_STATUS_MESSAGE = "Status inválido para processamento de pagamento.";

    @RabbitListener(queues = QueueNames.POLICY_PAYMENT)
    public void handlePaymentProcessed(GenericEvent event) {
        PolicyRequest policyRequest = activePolicyRequestSearchService.search(event.getPolicyRequestId());
        if (policyRequest.getStatus().equals(PENDING.name())) {
            if (customerBlockedExistsService.exists(policyRequest.getCustomerId(), PAYMENT)) {
                log.warn("O cliente com ID {} da solicitação {} teve o pagamento rejeitada por bloqueios.",
                        policyRequest.getCustomerId(), policyRequest.getId());
                publisher.publish(buildPaymentStatusEvent(policyRequest.getId(), REJECTED), QueueNames.POLICY_STATE_CHANGED);
            } else {
                log.warn("O cliente com ID {} da solicitação {} teve o pagamento aprovado.",
                        policyRequest.getCustomerId(), policyRequest.getId());
                publisher.publish(buildPaymentStatusEvent(policyRequest.getId(), APPROVED), QueueNames.POLICY_STATE_CHANGED);
            }
        } else {
            log.error("Não é possivel processar pagamento da solicitação {}, pois está com status diferente de {}",
                    event.getPolicyRequestId(), PENDING.name());
            throw new PolicyBadRequestException(INVALID_PAYMENT_STATUS_MESSAGE);
        }
    }

    private PaymentSubscriptionStatusEvent buildPaymentStatusEvent(UUID policyRequestId, PaymentSubscriptionStatus paymentSubscriptionStatus) {
        return PaymentSubscriptionStatusEvent.builder()
                .policyRequestId(policyRequestId)
                .paymentSubscriptionStatus(paymentSubscriptionStatus)
                .entityType(PAYMENT)
                .build();
    }
}
