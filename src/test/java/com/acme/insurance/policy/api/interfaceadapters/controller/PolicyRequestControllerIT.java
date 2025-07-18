package com.acme.insurance.policy.api.interfaceadapters.controller;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.repository.PolicyRequestRepository;
import com.acme.insurance.policy.api.interfaceadapters.dto.ErrorDetailsDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreateDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreatedResponseDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestResponseDTO;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PolicyRequestControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PolicyRequestRepository policyRequestRepository;

    private static final String BASE_PATH = "/api/policy-requests";

    @Test
    void shouldCreateAndApprovePolicyRequest() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(5000));

        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        assertThat(id).isNotNull();

        Thread.sleep(5000);

        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(APPROVED.name());
    }

    @Test
    void shouldCreateAndRejectedByPaymentServicePolicyRequest() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO
                (UUID.fromString("11111111-1111-1111-1111-111111111111"), "AUTO", BigDecimal.valueOf(5000));

        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());
        UUID id = response.getBody().getId();
        assertThat(id).isNotNull();

        Thread.sleep(5000);

        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    @Test
    void shouldCreateAndRejectedBySubscriptionServicePolicyRequest() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO
                (UUID.fromString("22222222-2222-2222-2222-222222222222"), "AUTO", BigDecimal.valueOf(5000));

        var response = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
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
        assertNotNull(response.getBody());
        UUID id = response.getBody().getId();

        Thread.sleep(5000);

        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo(REJECTED.name());
    }

    @Test
    void shouldReturnBadRequestWhenCustomerIdIsNull() {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(null, "AUTO", BigDecimal.valueOf(1000));
        dto.setProductId(UUID.randomUUID());

        var response = restTemplate.postForEntity(BASE_PATH, dto, ErrorDetailsDTO.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getMessage()).isEqualTo("Erro de validação nos campos fornecidos.");
        assertThat(response.getBody().getErrors()).containsKey("customerId");
    }

    @Test
    void shouldReturnBadRequestWhenInsuredAmountIsLessThanOne() {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.ZERO);

        var response = restTemplate.postForEntity(BASE_PATH, dto, ErrorDetailsDTO.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getMessage()).isEqualTo("Erro de validação nos campos fornecidos.");
        assertThat(response.getBody().getErrors()).containsKey("insuredAmount");
    }

    @Test
    void shouldReturnBadRequestWhenCoveragesIsEmpty() {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(1000));
        dto.setCoverages(Map.of());

        var response = restTemplate.postForEntity(BASE_PATH, dto, ErrorDetailsDTO.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getMessage()).isEqualTo("Erro de validação nos campos fornecidos.");
        assertThat(response.getBody().getErrors()).containsKey("coverages");
    }

    @Test
    void shouldReturnBadRequestWhenAssistancesIsEmpty() {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(1000));
        dto.setAssistances(List.of());

        var response = restTemplate.postForEntity(BASE_PATH, dto, ErrorDetailsDTO.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getMessage()).isEqualTo("Erro de validação nos campos fornecidos.");
        assertThat(response.getBody().getErrors()).containsKey("assistances");
    }

    @Test
    void shouldFindPolicyRequestById() {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(5000));

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        var getResponse = restTemplate.getForEntity(BASE_PATH + "/" + id, PolicyRequestResponseDTO.class);

        assertThat(getResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(id);
    }

    @Test
    void shouldReturnBadRequestWhenFindPolicyRequestByInvalidId() {
        UUID invalidId = UUID.randomUUID();

        var response = restTemplate.getForEntity(BASE_PATH + "/" + invalidId, ErrorDetailsDTO.class);

        assertNotNull(response.getBody());
        assertThat(response.getBody().getMessage()).isEqualTo("A solicitação de apólice informada não existe.");
    }

    @Test
    void shouldCancelPolicyRequest() {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(5000));

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        restTemplate.patchForObject(BASE_PATH + "/" + id + "/cancel", null, Void.class);

        PolicyRequest policyRequest = policyRequestRepository.findById(id).orElseThrow();
        assertThat(policyRequest.getStatus()).isEqualTo("CANCELED");
    }

    @Test
    void shouldReturnBadRequestWhenCancelAlreadyFinalizedPolicyRequest() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(5000));

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        Thread.sleep(5000);

        var response = restTemplate.exchange(
                BASE_PATH + "/" + id + "/cancel",
                org.springframework.http.HttpMethod.PATCH,
                null,
                Void.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void shouldFilterByCustomerId() {
        UUID customerId = UUID.randomUUID();
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(customerId, "AUTO", BigDecimal.valueOf(1000));

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        var response = restTemplate.getForEntity(BASE_PATH + "?customerId=" + customerId, PolicyRequestResponseDTO[].class);

        assertThat(response.getBody()).extracting("id").contains(id);
    }

    @Test
    void shouldFilterByProductId() {
        UUID productId = UUID.randomUUID();
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(1000));
        dto.setProductId(productId);

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        var response = restTemplate.getForEntity(BASE_PATH + "?productId=" + productId, PolicyRequestResponseDTO[].class);

        assertThat(response.getBody()).extracting("id").contains(id);
    }

    @Test
    void shouldFilterByCategory() {
        String category = "RESIDENTIAL";
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), category, BigDecimal.valueOf(1000));

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        var response = restTemplate.getForEntity(BASE_PATH + "?category=" + category, PolicyRequestResponseDTO[].class);

        assertThat(response.getBody()).extracting("id").contains(id);
    }

    @Test
    void shouldFilterByStatus() throws InterruptedException {
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(1000));

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        Thread.sleep(5000);

        var response = restTemplate.getForEntity(BASE_PATH + "?status=APPROVED", PolicyRequestResponseDTO[].class);

        assertThat(response.getBody()).extracting("id").contains(id);
    }

    @Test
    void shouldFilterBySalesChannel() {
        String salesChannel = "ONLINE";
        PolicyRequestCreateDTO dto = createPolicyRequestCreateDTO(UUID.randomUUID(), "AUTO", BigDecimal.valueOf(1000));

        var createResponse = restTemplate.postForEntity(BASE_PATH, dto, PolicyRequestCreatedResponseDTO.class);

        assertNotNull(createResponse.getBody());
        UUID id = createResponse.getBody().getId();

        var response = restTemplate.getForEntity(BASE_PATH + "?salesChannel=" + salesChannel, PolicyRequestResponseDTO[].class);

        assertThat(response.getBody()).extracting("id").contains(id);
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
}