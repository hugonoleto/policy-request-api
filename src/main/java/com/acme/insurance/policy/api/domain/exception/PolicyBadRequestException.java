package com.acme.insurance.policy.api.domain.exception;

public class PolicyBadRequestException extends RuntimeException {

    public PolicyBadRequestException(String message) {
        super(message);
    }

}
