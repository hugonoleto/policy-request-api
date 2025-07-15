package com.acme.insurance.policy.api.interfaceadapters.subscriber;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.event.SubscriptionStatusEvent;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.enums.SubscriptionStatus;
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
public class SubscriptionAuthorizedEventSubscriber {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final GenericEventPublisher publisher;

    @RabbitListener(queues = QueueNames.POLICY_SUBSCRIPTION)
    public void handleSubscriptionAuthorized(GenericEvent event) {
        PolicyRequest policyRequest = activePolicyRequestSearchService.search(event.getPolicyRequestId());
        if (policyRequest.getStatus().equals(PENDING.name())) {
            log.info("Subscrição autorizada para solicitação: {}", event.getPolicyRequestId());
            publisher.publish(buildSubscriptionStatusEvent(policyRequest.getId()), QueueNames.POLICY_STATE_CHANGED);
        } else {
            log.error("Não é possivel processar subscrição {} pois está com status diferente de {}",
                    event.getPolicyRequestId(), PENDING.name());
            //TODO: Implementar lógica de tratamento de erro ou notificação
        }
    }

    private SubscriptionStatusEvent buildSubscriptionStatusEvent(UUID policyRequestId) {
        return SubscriptionStatusEvent.builder()
                .policyRequestId(policyRequestId)
                .subscriptionStatus(SubscriptionStatus.AUTHORIZED)
                .build();
    }
}
