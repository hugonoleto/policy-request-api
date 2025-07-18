package com.acme.insurance.policy.api.interfaceadapters.controller;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestFilter;
import com.acme.insurance.policy.api.domain.service.CancelPolicyRequestService;
import com.acme.insurance.policy.api.domain.service.CreatePolicyRequestService;
import com.acme.insurance.policy.api.domain.service.PolicyRequestSearchByFiltersService;
import com.acme.insurance.policy.api.domain.service.PolicySearchByIdService;
import com.acme.insurance.policy.api.interfaceadapters.dto.ErrorDetailsDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreateDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreatedResponseDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestResponseDTO;
import com.acme.insurance.policy.api.interfaceadapters.mapper.PolicyRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/policy-requests")
@RequiredArgsConstructor
public class PolicyRequestController {

    private final CreatePolicyRequestService createPolicyRequestService;
    private final PolicyRequestSearchByFiltersService policyRequestSearchByFiltersService;
    private final PolicySearchByIdService policySearchByIdService;
    private final CancelPolicyRequestService cancelPolicyRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PolicyRequestCreatedResponseDTO.class))}),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailsDTO.class))})
    })
    @Operation(
            summary = "Cria uma nova solicitação de apólice",
            tags = {"Policy Requests"}
    )
    public PolicyRequestCreatedResponseDTO create(@RequestBody @Valid PolicyRequestCreateDTO request) {
        PolicyRequest entity = createPolicyRequestService.create(PolicyRequestMapper.INSTANCE.toEntity(request));
        return PolicyRequestMapper.INSTANCE.toResponseCreatedDTO(entity);
    }

    @GetMapping
    @Operation(
            summary = "Busca solicitações de apólice com filtros opcionais",
            tags = {"Policy Requests"}
    )
    public List<PolicyRequestResponseDTO> search(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String salesChannel) {
        PolicyRequestFilter filter = PolicyRequestFilter.builder()
                .customerId(customerId)
                .productId(productId)
                .category(category)
                .status(status)
                .salesChannel(salesChannel)
                .build();

        List<PolicyRequest> policyRequests = policyRequestSearchByFiltersService.search(filter);
        return PolicyRequestMapper.INSTANCE.toResponseDTOList(policyRequests);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PolicyRequestResponseDTO.class))}),
            @ApiResponse(responseCode = "404", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailsDTO.class))})
    })
    @Operation(
            summary = "Busca uma solicitação de apólice pelo ID",
            tags = {"Policy Requests"}
    )
    public PolicyRequestResponseDTO searchById(@PathVariable UUID id) {
        PolicyRequest policyRequest = policySearchByIdService.search(id);
        return PolicyRequestMapper.INSTANCE.toResponseDTO(policyRequest);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "400", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetailsDTO.class))})
    })
    @Operation(
            summary = "Cancela uma solicitação de apólice pelo ID",
            tags = {"Policy Requests"}
    )
    public void cancel(@PathVariable UUID id) {
        cancelPolicyRequestService.cancel(id);
    }

}