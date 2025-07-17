package com.acme.insurance.policy.api.interfaceadapters.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetailsDTO {

    private int statusCode;
    private String message;
    private Map<String, String> errors;

}