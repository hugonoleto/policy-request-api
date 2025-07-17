package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.state.PolicyState;
import com.acme.insurance.policy.api.domain.state.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PolicyStateProcessorService {

    private final Map<State, PolicyState> stateMap = new EnumMap<>(State.class);

    @Autowired
    public PolicyStateProcessorService(List<PolicyState> policyStates) {
        this.stateMap.putAll(
                policyStates.stream()
                        .collect(Collectors.toMap(PolicyState::current, state -> state))
        );
    }

    public void process(PolicyRequest policyRequest) {
        log.info("Iniciando processamento para solicitação com ID: {} e status: {}",
                policyRequest.getId(), policyRequest.getStatus());

        State currentState = State.valueOf(policyRequest.getStatus());
        log.debug("Estado atual obtido para solicitação com ID {}: {}", policyRequest.getId(), currentState);

        PolicyState policyState = stateMap.get(currentState);
        if (policyState == null) {
            log.error("Estado não suportado para solicitação com ID {}: {}", policyRequest.getId(), currentState);
            throw new PolicyBadRequestException("Estado não suportado: " + currentState);
        }

        log.info("Executando estado para PolicyRequest com ID {}: {}", policyRequest.getId(), currentState);
        policyState.execute(policyRequest);
        log.info("Processamento concluído para solicitação com ID: {} e status: {}",
                policyRequest.getId(), policyRequest.getStatus());
    }
}