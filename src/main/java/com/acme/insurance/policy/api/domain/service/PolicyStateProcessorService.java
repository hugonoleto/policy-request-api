package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.state.PolicyState;
import com.acme.insurance.policy.api.domain.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        State currentState = State.valueOf(policyRequest.getStatus());
        PolicyState policyState = stateMap.get(currentState);
        if (policyState == null) {
            throw new IllegalStateException("Estado não suportado: " + currentState);
        }
        policyState.execute(policyRequest);
    }


}