package com.acme.insurance.policy.api.interfaceadapters.controller;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreateDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreatedResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.acme.insurance.policy.api.domain.state.State.APPROVED;
import static com.acme.insurance.policy.api.domain.state.State.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PolicyRequestControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PolicyRequestRepository policyRequestRepository;

    private static final String BASE_PATH = "/api/policy-requests";

    @Test
    void shouldCreateAndApprovePolicyRequest() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID());

        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        assertThat(id).isNotNull();

        Thread.sleep(5000);

        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(APPROVED.name());
    }

    @Test
    void shouldCreateAndRejectedByPaymentServicePolicyRequest() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO
                (UUID.fromString("11111111-1111-1111-1111-111111111111"));

        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        assertThat(id).isNotNull();

        Thread.sleep(5000);

        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    @Test
    void shouldCreateAndRejectedBySubscriptionServicePolicyRequest() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO
                (UUID.fromString("22222222-2222-2222-2222-222222222222"));

        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        assertThat(id).isNotNull();

        Thread.sleep(5000);

        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    @Test
    void shouldApproveHighRiskAutoWhenInsuredAmountIsValid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.fromString("44444444-4444-4444-4444-444444444444"), "AUTO", BigDecimal.valueOf(250000));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(APPROVED.name());
    }

    @Test
    void shouldRejectHighRiskAutoWhenInsuredAmountIsInvalid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.fromString("44444444-4444-4444-4444-444444444444"), "AUTO", BigDecimal.valueOf(250001));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    @Test
    void shouldApprovePreferentialResidentialWhenInsuredAmountIsValid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.fromString("55555555-5555-5555-5555-555555555555"), "RESIDENTIAL", BigDecimal.valueOf(450000));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(APPROVED.name());
    }

    @Test
    void shouldRejectPreferentialResidentialWhenInsuredAmountIsInvalid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.fromString("55555555-5555-5555-5555-555555555555"), "RESIDENTIAL", BigDecimal.valueOf(450001));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    @Test
    void shouldApproveNoInformationLifeWhenInsuredAmountIsValid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.fromString("66666666-6666-6666-6666-666666666666"), "LIFE", BigDecimal.valueOf(200000));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(APPROVED.name());
    }

    @Test
    void shouldRejectNoInformationLifeWhenInsuredAmountIsInvalid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.fromString("66666666-6666-6666-6666-666666666666"), "LIFE", BigDecimal.valueOf(200001));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    @Test
    void shouldApproveRegularOtherCategoryWhenInsuredAmountIsValid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.randomUUID(), "OUTRA", BigDecimal.valueOf(255000));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(APPROVED.name());
    }

    @Test
    void shouldRejectRegularOtherCategoryWhenInsuredAmountIsInvalid() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(
                UUID.randomUUID(), "OUTRA", BigDecimal.valueOf(255001));
        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        Thread.sleep(5000);
        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    private PolicyRequestCreateDTO createPolicyRequestCreateDTO(UUID customerId, String category, BigDecimal insuredAmount) {
        return PolicyRequestCreateDTO.builder()
                .customerId(customerId)
                .productId(UUID.randomUUID())
                .category(category)
                .salesChannel("ONLINE")
                .paymentMethod("CREDIT_CARD")
                .totalMonthlyPremiumAmount(BigDecimal.valueOf(150.75))
                .insuredAmount(insuredAmount)
                .coverages(Map.of(
                        "ROUBO", BigDecimal.valueOf(20000.00),
                        "COLISAO", BigDecimal.valueOf(30000.00)
                ))
                .assistances(List.of("GUINCHO", "CARRO_RESERVA"))
                .build();
    }

    private PolicyRequestCreateDTO createPolicyRequestCreateDTO(UUID customerId) {
        return PolicyRequestCreateDTO.builder()
                .customerId(customerId)
                .productId(UUID.randomUUID())
                .category("AUTO")
                .salesChannel("ONLINE")
                .paymentMethod("CREDIT_CARD")
                .totalMonthlyPremiumAmount(BigDecimal.valueOf(150.75))
                .insuredAmount(BigDecimal.valueOf(50000.00))
                .coverages(Map.of(
                        "ROUBO", BigDecimal.valueOf(20000.00),
                        "COLISAO", BigDecimal.valueOf(30000.00)
                ))
                .assistances(List.of("GUINCHO", "CARRO_RESERVA"))
                .build();
    }
}