package com.acme.insurance.policy.api.interfaceadapters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyRequestHistoryDTO {

    private String status;
    private OffsetDateTime timestamp;

}