package com.acme.insurance.policy.api.interfaceadapters.subscriber;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.event.PaymentStatusEvent;
import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.enums.PaymentStatus;
import com.acme.insurance.policy.api.domain.service.ActivePolicyRequestSearchService;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.acme.insurance.policy.api.domain.state.State.PENDING;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProcessedEventSubscriber {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final GenericEventPublisher publisher;

    private static final String INVALID_PAYMENT_STATUS_MESSAGE = "Status inválido para processamento de pagamento.";

    @RabbitListener(queues = QueueNames.POLICY_PAYMENT)
    public void handlePaymentProcessed(GenericEvent event) {
        PolicyRequest policyRequest = activePolicyRequestSearchService.search(event.getPolicyRequestId());
        if (policyRequest.getStatus().equals(PENDING.name())) {
            log.info("Pagamento processado para solicitação: {} ", policyRequest.getId());
            publisher.publish(buildPaymentStatusEvent(policyRequest.getId()), QueueNames.POLICY_STATE_CHANGED);
        } else {
            log.error("Não é possivel processar pagamento da solicitação {}, pois está com status diferente de {}",
                    event.getPolicyRequestId(), PENDING.name());
            throw new PolicyBadRequestException(INVALID_PAYMENT_STATUS_MESSAGE);
        }
    }

    private PaymentStatusEvent buildPaymentStatusEvent(UUID policyRequestId) {
        return PaymentStatusEvent.builder()
                .policyRequestId(policyRequestId)
                .paymentStatus(PaymentStatus.CONFIRMED)
                .build();
    }
}
