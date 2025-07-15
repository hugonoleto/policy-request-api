package com.acme.insurance.policy.api.interfaceadapters.controller;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.service.CreatePolicyRequestService;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreateDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreatedResponseDTO;
import com.acme.insurance.policy.api.interfaceadapters.mapper.PolicyRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policy-requests")
@RequiredArgsConstructor
public class PolicyRequestController {

    private final CreatePolicyRequestService createPolicyRequestService;

    @PostMapping
    public PolicyRequestCreatedResponseDTO createPolicyRequest(@RequestBody PolicyRequestCreateDTO request) {
        PolicyRequest entity = createPolicyRequestService.process(PolicyRequestMapper.INSTANCE.toEntity(request));
        return PolicyRequestMapper.INSTANCE.toResponse(entity);
    }

}
