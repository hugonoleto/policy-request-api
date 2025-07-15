package com.acme.insurance.policy.api.interfaceadapters.subscriber;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.service.PolicySearchByIdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventSubscriber {

    private final PolicySearchByIdService policySearchByIdService;

    @RabbitListener(queues = QueueNames.POLICY_NOTIFICATION)
    public void handleNotificationEvent(GenericEvent event) {
        PolicyRequest policyRequest = policySearchByIdService.search(event.getPolicyRequestId());
        log.info("Notificação enviada: Cliente {} foi informado que a solicitação de apólice foi concluída com o status '{}'.",
                policyRequest.getCustomerId(), policyRequest.getStatus());
    }
}