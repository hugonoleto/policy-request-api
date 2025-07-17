package com.acme.insurance.policy.api.domain.state;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.acme.insurance.policy.api.domain.state.State.VALIDATED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatedState implements PolicyState {

    private final PolicyRequestRepository repository;
    private final GenericEventPublisher publisher;

    @Override
    public State current() {
        return VALIDATED;
    }

    @Override
    public void execute(PolicyRequest policyRequest) {
        log.info("Iniciando processamento para solicitação com ID: {} e status: {}",
                policyRequest.getId(), policyRequest.getStatus());

        policyRequest.addHistory(State.PENDING.name());
        log.debug("Histórico atualizado para solicitação com ID {}: {}",
                policyRequest.getId(), policyRequest.getHistory());

        repository.save(policyRequest);

        publisher.publish(new GenericEvent(policyRequest.getId()), QueueNames.POLICY_PAYMENT);
        log.info("Solicitação {} enviada para serviço de Pagamento", policyRequest.getId());

        publisher.publish(new GenericEvent(policyRequest.getId()), QueueNames.POLICY_SUBSCRIPTION);
        log.info("Solicitação {} enviada para serviço de Subscrição", policyRequest.getId());

    }
}
