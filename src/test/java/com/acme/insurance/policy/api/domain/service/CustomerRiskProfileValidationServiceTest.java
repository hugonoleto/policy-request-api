package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.exception.PolicyBadRequestException;
import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.service.validator.RiskProfileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.acme.insurance.policy.api.domain.model.enums.RiskProfile.HIGH_RISK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRiskProfileValidationServiceTest {

    @Mock
    private RiskProfileValidator riskProfileValidator;

    @InjectMocks
    private CustomerRiskProfileValidationService service;

    private PolicyRequest policyRequest;

    @BeforeEach
    void setUp() {
        policyRequest = new PolicyRequest();
        policyRequest.setId(UUID.randomUUID());
        policyRequest.setInsuredAmount(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldValidateSuccessfullyWhenRiskProfileIsSupported() {
        String riskProfile = HIGH_RISK.name();

        when(riskProfileValidator.supports(riskProfile)).thenReturn(true);
        when(riskProfileValidator.validate(policyRequest)).thenReturn(true);

        service = new CustomerRiskProfileValidationService(List.of(riskProfileValidator));

        boolean result = service.validate(policyRequest, riskProfile);

        assertTrue(result);
        verify(riskProfileValidator).supports(riskProfile);
        verify(riskProfileValidator).validate(policyRequest);
    }

    @Test
    void shouldThrowExceptionWhenRiskProfileIsNotSupported() {
        String riskProfile = "INVALID_RISK";

        when(riskProfileValidator.supports(riskProfile)).thenReturn(false);

        service = new CustomerRiskProfileValidationService(List.of(riskProfileValidator));

        PolicyBadRequestException exception = assertThrows(PolicyBadRequestException.class, () ->
                service.validate(policyRequest, riskProfile)
        );

        assertEquals("Perfil de risco não suportado: " + riskProfile, exception.getMessage());
        verify(riskProfileValidator).supports(riskProfile);
        verify(riskProfileValidator, never()).validate(policyRequest);
    }
}