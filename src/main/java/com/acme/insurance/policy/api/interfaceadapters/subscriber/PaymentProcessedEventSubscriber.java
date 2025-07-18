package com.acme.insurance.policy.api.interfaceadapters.subscriber;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.model.enums.EntityType;
import com.acme.insurance.policy.api.domain.service.PaymentSubscriptionProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProcessedEventSubscriber {

    private final PaymentSubscriptionProcessingService paymentSubscriptionProcessingService;

    @RabbitListener(queues = QueueNames.POLICY_PAYMENT)
    public void handlePaymentProcessed(GenericEvent event) {
        paymentSubscriptionProcessingService.process(event.getPolicyRequestId(), EntityType.PAYMENT);
    }
}