package com.acme.insurance.policy.api.interfaceadapters.subscriber;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.model.enums.EntityType;
import com.acme.insurance.policy.api.domain.service.PaymentSubscriptionProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionAuthorizedEventSubscriber {

    private final PaymentSubscriptionProcessingService paymentSubscriptionProcessingService;

    @RabbitListener(queues = QueueNames.POLICY_SUBSCRIPTION)
    public void handlePaymentProcessed(GenericEvent event) {
        paymentSubscriptionProcessingService.process(event.getPolicyRequestId(), EntityType.SUBSCRIPTION);
    }
}
