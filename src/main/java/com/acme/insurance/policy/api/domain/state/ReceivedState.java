package com.acme.insurance.policy.api.domain.state;

import com.acme.insurance.policy.api.domain.event.GenericEvent;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import com.acme.insurance.policy.api.domain.model.FraudAnalysis;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import com.acme.insurance.policy.api.domain.service.CustomerRiskProfileValidationService;
import com.acme.insurance.policy.api.domain.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.acme.insurance.policy.api.domain.constants.QueueNames.POLICY_STATE_CHANGED;
import static com.acme.insurance.policy.api.domain.state.State.RECEIVED;
import static com.acme.insurance.policy.api.domain.state.State.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.VALIDATED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceivedState implements PolicyState {

    private final FraudAnalysisService fraudAnalysisService;
    private final CustomerRiskProfileValidationService customerRiskProfileValidationService;
    private final PolicyRequestRepository repository;
    private final GenericEventPublisher publisher;

    @Override
    public State current() {
        return RECEIVED;
    }

    @Override
    public void execute(PolicyRequest policyRequest) {
        log.info("Iniciando processamento para solicitação com ID: {} e status: {}", policyRequest.getId(), policyRequest.getStatus());

        FraudAnalysis fraudAnalysis = fraudAnalysisService.getFraudAnalysis(policyRequest.getCustomerId().toString());
        log.debug("Análise de fraude obtida. Cliente ID: {}, solicitação ID: {}, Classificação: {}",
                policyRequest.getCustomerId(), policyRequest.getId(), fraudAnalysis.getClassification());

        if (customerRiskProfileValidationService.validate(policyRequest, fraudAnalysis.getClassification())) {
            log.info("Validação de perfil de risco bem-sucedida. Cliente ID: {}, solicitação ID: {}, Classificação: {}, Status: {}",
                    policyRequest.getCustomerId(), policyRequest.getId(), fraudAnalysis.getClassification(), VALIDATED.name());
            policyRequest.addHistory(VALIDATED.name());
        } else {
            log.info("Validação de perfil de risco foi rejeitada. Cliente ID: {}, solicitação ID: {}, Classificação: {}, Status: {}",
                    policyRequest.getCustomerId(), policyRequest.getId(), fraudAnalysis.getClassification(), REJECTED.name());
            policyRequest.addHistory(REJECTED.name());
        }

        repository.save(policyRequest);

        if (VALIDATED.name().equals(policyRequest.getStatus())) {
            publisher.publish(new GenericEvent(policyRequest.getId()), POLICY_STATE_CHANGED);
            log.info("Evento de mudança de estado publicado para solicitação com ID: {}", policyRequest.getId());
        }
    }
}
