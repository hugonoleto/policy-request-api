package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestHistory;
import com.acme.insurance.policy.api.domain.model.enums.PaymentStatus;
import com.acme.insurance.policy.api.domain.model.enums.SubscriptionStatus;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_NOTIFICATION;
import static com.acme.insurance.policy.api.domain.state.State.APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.PAYMENT_CONFIRMED;
import static com.acme.insurance.policy.api.domain.state.State.PAYMENT_REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.SUBSCRIPTION_AUTHORIZED;
import static com.acme.insurance.policy.api.domain.state.State.SUBSCRIPTION_REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyCallbackHandlerService {

    private final PolicyRequestRepository repository;
    private final GenericEventPublisher publisher;

    public void handlePaymentStatusUpdate(PolicyRequest policyRequest, PaymentStatus paymentStatus) {
        log.info("Atualizando status de pagamento para solicitação com ID: {}, Status: {}",
                policyRequest.getId(), paymentStatus);

        if (paymentStatus.equals(PaymentStatus.CONFIRMED)) {
            log.debug("Pagamento confirmado para solicitação com ID: {}", policyRequest.getId());
            policyRequest.addHistory(PAYMENT_CONFIRMED.name());
            approveIfPaymentAndSubscriptionAuthorized(policyRequest);
        } else {
            log.warn("Pagamento rejeitado para solicitação com ID: {}", policyRequest.getId());
            policyRequest.addHistory(PAYMENT_REJECTED.name());
            policyRequest.addHistory(REJECTED.name());
        }

        repository.save(policyRequest);
        notifyClientFinalStatus(policyRequest);
    }

    public void handleSubscriptionStatusUpdate(PolicyRequest policyRequest, SubscriptionStatus subscriptionStatus) {
        log.info("Atualizando status de subscrição para solicitação com ID: {}, Status: {}",
                policyRequest.getId(), subscriptionStatus);

        if (subscriptionStatus.equals(SubscriptionStatus.AUTHORIZED)) {
            log.debug("Subscrição autorizada para solicitação com ID: {}", policyRequest.getId());
            policyRequest.addHistory(SUBSCRIPTION_AUTHORIZED.name());
            approveIfPaymentAndSubscriptionAuthorized(policyRequest);
        } else {
            log.warn("Subscrição rejeitada para solicitação com ID: {}", policyRequest.getId());
            policyRequest.addHistory(SUBSCRIPTION_REJECTED.name());
            policyRequest.addHistory(REJECTED.name());
        }

        repository.save(policyRequest);
        notifyClientFinalStatus(policyRequest);
    }

    private void approveIfPaymentAndSubscriptionAuthorized(PolicyRequest policyRequest) {
        log.debug("Verificando aprovação para solicitação com ID: {}", policyRequest.getId());

        List<String> historyStatuses = policyRequest.getHistory().stream()
                .map(PolicyRequestHistory::getStatus)
                .toList();

        if (historyStatuses.contains(PAYMENT_CONFIRMED.name()) && historyStatuses.contains(SUBSCRIPTION_AUTHORIZED.name())) {
            log.info("Solicitação com ID: {} aprovada.", policyRequest.getId());
            policyRequest.addHistory(APPROVED.name());
        }
    }

    private void notifyClientFinalStatus(PolicyRequest policyRequest) {
        log.debug("Notificando cliente sobre status final para solicitação com ID: {}", policyRequest.getId());

        if (isFinalized(policyRequest.getStatus())) {
            log.info("Status finalizado para solicitação com ID: {}, publicando evento.", policyRequest.getId());
            publisher.publish(new GenericEvent(policyRequest.getId()), POLICY_NOTIFICATION);
        }
    }

    private boolean isFinalized(String status) {
        return APPROVED.name().equals(status) || REJECTED.name().equals(status);
    }
}
