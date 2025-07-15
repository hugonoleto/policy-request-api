package com.acme.insurance.policy.api.interfaceadapters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestCreatedResponseDTO {

    private UUID id;
    private OffsetDateTime createdAt;

}
