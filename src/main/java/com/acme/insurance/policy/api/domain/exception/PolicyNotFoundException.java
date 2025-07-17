package com.acme.insurance.policy.api.domain.exception;

public class PolicyNotFoundException extends RuntimeException {

    public PolicyNotFoundException(String message) {
        super(message);
    }

}
