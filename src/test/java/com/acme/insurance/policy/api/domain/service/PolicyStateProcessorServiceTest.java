package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.state.ReceivedState;
import com.acme.insurance.policy.api.domain.state.State;
import com.acme.insurance.policy.api.domain.state.ValidatedState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyStateProcessorServiceTest {

    @Mock
    private ReceivedState receivedState;

    @Mock
    private ValidatedState validatedState;

    private PolicyStateProcessorService policyStateProcessorService;

    @BeforeEach
    void setUp() {
        when(receivedState.current()).thenReturn(State.RECEIVED);
        when(validatedState.current()).thenReturn(State.VALIDATED);

        policyStateProcessorService = new PolicyStateProcessorService(List.of(receivedState, validatedState));
    }

    @Test
    void shouldProcessPolicyRequestWithReceiveState() {
        PolicyRequest policyRequest = new PolicyRequest();
        policyRequest.setStatus(State.RECEIVED.name());

        policyStateProcessorService.process(policyRequest);

        verify(receivedState).execute(policyRequest);
    }

    @Test
    void shouldLogAndProcessPolicyRequestWithValidatedState() {
        PolicyRequest policyRequest = mock(PolicyRequest.class);
        when(policyRequest.getStatus()).thenReturn(State.VALIDATED.name());

        policyStateProcessorService.process(policyRequest);

        verify(validatedState).execute(policyRequest);
    }

    @Test
    void shouldThrowExceptionForUnsupportedState() {
        PolicyRequest policyRequest = new PolicyRequest();
        policyRequest.setStatus(State.CANCELED.name());

        assertThrows(PolicyBadRequestException.class, () -> policyStateProcessorService.process(policyRequest));
    }
}