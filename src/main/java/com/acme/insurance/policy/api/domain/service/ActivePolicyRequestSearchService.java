package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.acme.insurance.policy.api.domain.state.State.APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.CANCELED;
import static com.acme.insurance.policy.api.domain.state.State.PAYMENT_REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.REJECTED;
import static com.acme.insurance.policy.api.domain.state.State.SUBSCRIPTION_REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivePolicyRequestSearchService {

    private final PolicyRequestRepository policyRequestRepository;

    private static final String ERROR_MESSAGE = "A solicitação de apólice não existe ou está finalizada.";

    public PolicyRequest search(UUID id) {
        List<String> excludedStatuses = Arrays.asList(REJECTED.name(), APPROVED.name(), PAYMENT_REJECTED.name(),
                SUBSCRIPTION_REJECTED.name(), CANCELED.name());

        return policyRequestRepository.findByIdAndStatusNotIn(id, excludedStatuses).orElseThrow(() -> {
            log.error("A solicitação {} não encontrada ou já finalizada.", id);
            return new PolicyBadRequestException(ERROR_MESSAGE);
        });
    }
}
