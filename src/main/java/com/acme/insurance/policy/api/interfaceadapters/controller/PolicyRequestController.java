package com.acme.insurance.policy.api.interfaceadapters.controller;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestFilter;
import com.acme.insurance.policy.api.domain.service.CancelPolicyRequestService;
import com.acme.insurance.policy.api.domain.service.CreatePolicyRequestService;
import com.acme.insurance.policy.api.domain.service.PolicyRequestSearchByFiltersService;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreateDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreatedResponseDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestResponseDTO;
import com.acme.insurance.policy.api.interfaceadapters.mapper.PolicyRequestMapper;
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
    private final CancelPolicyRequestService cancelPolicyRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyRequestCreatedResponseDTO create(@RequestBody PolicyRequestCreateDTO request) {
        PolicyRequest entity = createPolicyRequestService.process(PolicyRequestMapper.INSTANCE.mapToEntity(request));
        return PolicyRequestMapper.INSTANCE.mapToResponse(entity);
    }

    @GetMapping
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
        return PolicyRequestMapper.INSTANCE.mapToResponseDTOList(policyRequests);
    }

    @PatchMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {
        cancelPolicyRequestService.cancel(id);
    }

}
