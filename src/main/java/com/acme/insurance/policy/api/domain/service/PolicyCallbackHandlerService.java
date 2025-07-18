package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.event.EventPublisher;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestHistory;
import com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_NOTIFICATION;
import static com.acme.insurance.policy.api.domain.state.State.APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.PAYMENT_APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.PAYMENT_REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.SUBSCRIPTION_APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.SUBSCRIPTION_REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyCallbackHandlerService {

    private final PolicyRequestRepository repository;
    private final EventPublisher publisher;

    public void handlePaymentStatusUpdate(PolicyRequest policyRequest, PaymentSubscriptionStatus paymentSubscriptionStatus) {
        log.info("Atualizando status de pagamento para solicitação com ID: {}, Status: {}",
                policyRequest.getId(), paymentSubscriptionStatus);

        if (paymentSubscriptionStatus.equals(PaymentSubscriptionStatus.APPROVED)) {
            log.debug("Pagamento confirmado para solicitação com ID: {}", policyRequest.getId());
            policyRequest.addHistory(PAYMENT_APPROVED.name());
            approveIfPaymentAndSubscriptionAuthorized(policyRequest);
        } else {
            log.warn("Pagamento rejeitado para solicitação com ID: {}", policyRequest.getId());
            policyRequest.addHistory(PAYMENT_REJECTED.name());
            policyRequest.addHistory(REJECTED.name());
        }

        repository.save(policyRequest);
        notifyClientFinalStatus(policyRequest);
    }

    public void handleSubscriptionStatusUpdate(PolicyRequest policyRequest, PaymentSubscriptionStatus paymentSubscriptionStatus) {
        log.info("Atualizando status de assinatura para solicitação com ID: {}, Status: {}",
                policyRequest.getId(), paymentSubscriptionStatus);

        if (paymentSubscriptionStatus.equals(PaymentSubscriptionStatus.APPROVED)) {
            log.debug("Assinatura autorizada para solicitação com ID: {}", policyRequest.getId());
            policyRequest.addHistory(SUBSCRIPTION_APPROVED.name());
            approveIfPaymentAndSubscriptionAuthorized(policyRequest);
        } else {
            log.warn("Assinatura rejeitada para solicitação com ID: {}", policyRequest.getId());
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

        if (historyStatuses.contains(PAYMENT_APPROVED.name()) && historyStatuses.contains(SUBSCRIPTION_APPROVED.name())) {
            log.info("Solicitação {} aprovada.", policyRequest.getId());
            policyRequest.addHistory(APPROVED.name());
        }
    }

    private void notifyClientFinalStatus(PolicyRequest policyRequest) {
        if (isFinalized(policyRequest.getStatus())) {
            log.info("Solicitação {} foi finalizada com o status '{}'. Enviando notificação por e-mail ao cliente.",
                    policyRequest.getId(), policyRequest.getStatus());
            publisher.publish(new GenericEvent(policyRequest.getId()), POLICY_NOTIFICATION);
        }
    }

    private boolean isFinalized(String status) {
        return APPROVED.name().equals(status) || REJECTED.name().equals(status);
    }
}
