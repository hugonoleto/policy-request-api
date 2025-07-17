package com.acme.insurance.policy.api.interfaceadapters.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDetailsDTO {

    private int statusCode;
    private String message;

}