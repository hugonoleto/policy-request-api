package com.acme.insurance.policy.api.interfaceadapters.subscriber;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.event.PaymentStatusEvent;
import com.acme.insurance.policy.api.domain.event.SubscriptionStatusEvent;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.service.ActivePolicyRequestSearchService;
import com.acme.insurance.policy.api.domain.service.PolicyStateProcessorService;
import com.acme.insurance.policy.api.domain.service.PolicyCallbackHandlerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
public class PolicyRequestEventSubscriber {

    private final ActivePolicyRequestSearchService activePolicyRequestSearchService;
    private final PolicyStateProcessorService policyStateProcessorService;
    private final PolicyCallbackHandlerService policyCallbackHandlerService;

    @RabbitListener(queues = QueueNames.POLICY_STATE_CHANGED)
    public void handlePolicyRequestEvent(GenericEvent event) {
        PolicyRequest policyRequest = activePolicyRequestSearchService.search(event.getPolicyRequestId());
        eventHandlers
                .getOrDefault(event.getClass(), (req, evt) -> policyStateProcessorService.process(req))
                .accept(policyRequest, event);
    }

    private Map<Class<?>, BiConsumer<PolicyRequest, GenericEvent>> eventHandlers;

    @PostConstruct
    private void initHandlers() {
        eventHandlers = Map.of(
                PaymentStatusEvent.class, (req, evt) -> policyCallbackHandlerService.handlePaymentStatusUpdate(req, ((PaymentStatusEvent) evt).getPaymentStatus()),
                SubscriptionStatusEvent.class, (req, evt) -> policyCallbackHandlerService.handleSubscriptionStatusUpdate(req, ((SubscriptionStatusEvent) evt).getSubscriptionStatus())
        );
    }
}