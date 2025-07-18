package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.constants.QueueNames;
import com.acme.insurance.policy.api.domain.event.PaymentSubscriptionStatusEvent;
import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.enums.EntityType;
import com.acme.insurance.policy.api.infrastructure.messaging.GenericEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus.APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentSubscriptionProcessingServiceTest {

    @Mock
    private ActivePolicyRequestSearchService activePolicyRequestSearchService;

    @Mock
    private CustomerBlockedExistsService customerBlockedExistsService;

    @Mock
    private GenericEventPublisher publisher;

    @InjectMocks
    private PaymentSubscriptionProcessingService service;

    private UUID policyRequestId;
    private PolicyRequest policyRequest;

    @BeforeEach
    void setUp() {
        policyRequestId = UUID.randomUUID();
        policyRequest = new PolicyRequest();
        policyRequest.setId(policyRequestId);
        policyRequest.setStatus(PENDING.name());
        policyRequest.setCustomerId(UUID.randomUUID());
    }

    @Test
    void shouldApproveRequestWhenCustomerIsNotBlocked() {
        EntityType entityType = EntityType.PAYMENT;

        when(activePolicyRequestSearchService.search(policyRequestId)).thenReturn(policyRequest);
        when(customerBlockedExistsService.exists(policyRequest.getCustomerId(), entityType)).thenReturn(false);

        service.process(policyRequestId, entityType);

        verify(publisher).publish(
                PaymentSubscriptionStatusEvent.builder()
                        .policyRequestId(policyRequestId)
                        .paymentSubscriptionStatus(APPROVED)
                        .entityType(entityType)
                        .build(),
                QueueNames.POLICY_STATE_CHANGED
        );
    }

    @Test
    void shouldRejectRequestWhenCustomerIsBlocked() {
        EntityType entityType = EntityType.SUBSCRIPTION;

        when(activePolicyRequestSearchService.search(policyRequestId)).thenReturn(policyRequest);
        when(customerBlockedExistsService.exists(policyRequest.getCustomerId(), entityType)).thenReturn(true);

        service.process(policyRequestId, entityType);

        verify(publisher).publish(
                PaymentSubscriptionStatusEvent.builder()
                        .policyRequestId(policyRequestId)
                        .paymentSubscriptionStatus(APPROVED)
                        .entityType(entityType)
                        .build(),
                QueueNames.POLICY_STATE_CHANGED
        );
    }

    @Test
    void shouldThrowExceptionWhenPolicyRequestStatusIsNotPending() {
        EntityType entityType = EntityType.PAYMENT;
        policyRequest.setStatus("COMPLETED");

        when(activePolicyRequestSearchService.search(policyRequestId)).thenReturn(policyRequest);

        PolicyBadRequestException exception = assertThrows(PolicyBadRequestException.class, () ->
                service.process(policyRequestId, entityType)
        );

        verifyNoInteractions(publisher);
        assertEquals("Status inválido para processamento de " + entityType.getDescription() + ".", exception.getMessage());
    }
}