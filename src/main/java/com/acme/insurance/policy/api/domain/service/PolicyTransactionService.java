package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestHistory;
import com.acme.insurance.policy.api.domain.model.enums.PaymentStatus;
import com.acme.insurance.policy.api.domain.model.enums.SubscriptionStatus;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_NOTIFICATION;
import static com.acme.insurance.policy.api.domain.state.State.APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.PAYMENT_CONFIRMED;
import static com.acme.insurance.policy.api.domain.state.State.PAYMENT_REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.SUBSCRIPTION_AUTHORIZED;
import static com.acme.insurance.policy.api.domain.state.State.SUBSCRIPTION_REJECTED;

@Service
@RequiredArgsConstructor
public class PolicyTransactionService {

    private final PolicyRequestRepository repository;
    private final GenericEventPublisher publisher;

    public void handlePaymentStatusUpdate(PolicyRequest policyRequest, PaymentStatus paymentStatus) {
        if (paymentStatus.equals(PaymentStatus.CONFIRMED)) {
            policyRequest.addHistory(PAYMENT_CONFIRMED.name());
            approveIfPaymentAndSubscriptionAuthorized(policyRequest);
        } else {
            policyRequest.addHistory(PAYMENT_REJECTED.name());
            policyRequest.addHistory(REJECTED.name());
        }
        repository.save(policyRequest);
        notifyClientFinalStatus(policyRequest);
    }

    public void handleSubscriptionStatusUpdate(PolicyRequest policyRequest, SubscriptionStatus subscriptionStatus) {
        if (subscriptionStatus.equals(SubscriptionStatus.AUTHORIZED)) {
            policyRequest.addHistory(SUBSCRIPTION_AUTHORIZED.name());
            approveIfPaymentAndSubscriptionAuthorized(policyRequest);
        } else {
            policyRequest.addHistory(SUBSCRIPTION_REJECTED.name());
            policyRequest.addHistory(REJECTED.name());
        }
        repository.save(policyRequest);
        notifyClientFinalStatus(policyRequest);
    }

    private void approveIfPaymentAndSubscriptionAuthorized(PolicyRequest policyRequest) {
        List<String> historyStatuses = policyRequest.getHistory().stream()
                .map(PolicyRequestHistory::getStatus)
                .toList();

        if (historyStatuses.contains(PAYMENT_CONFIRMED.name()) && historyStatuses.contains(SUBSCRIPTION_AUTHORIZED.name())) {
            policyRequest.addHistory(APPROVED.name());
        }
    }

    private void notifyClientFinalStatus(PolicyRequest policyRequest) {
        if (isFinalized(policyRequest.getStatus())) {
            publisher.publish(new GenericEvent(policyRequest.getId()), POLICY_NOTIFICATION);
        }
    }

    private boolean isFinalized(String status) {
        return APPROVED.name().equals(status) || REJECTED.name().equals(status);
    }

}
