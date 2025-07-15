package com.acme.insurance.policy.api.domain.state;

import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import com.acme.insurance.policy.api.domain.model.FraudAnalysis;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import com.acme.insurance.policy.api.domain.service.CustomerValidationService;
import com.acme.insurance.policy.api.domain.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_STATE_CHANGED;
import static com.acme.insurance.policy.api.domain.state.State.RECEIVED;
import static com.acme.insurance.policy.api.domain.state.State.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.VALIDATED;

@Component
@RequiredArgsConstructor
public class ReceivedState implements PolicyState {

    private final FraudAnalysisService fraudAnalysisService;
    private final CustomerValidationService customerValidationService;
    private final PolicyRequestRepository repository;
    private final GenericEventPublisher publisher;

    @Override
    public State current() {
        return RECEIVED;
    }

    @Override
    public void execute(PolicyRequest entity) {
        FraudAnalysis fraudAnalysis = fraudAnalysisService.getFraudAnalysis(entity.getCustomerId().toString());

        if (customerValidationService.validate(entity, fraudAnalysis.getClassification())) {
            entity.addHistory(VALIDATED.name());
        } else {
            entity.addHistory(REJECTED.name());
        }

        repository.save(entity);

        if (VALIDATED.name().equals(entity.getStatus())) {
            publisher.publish(new GenericEvent(entity.getId()), POLICY_STATE_CHANGED);
        }
    }
}
