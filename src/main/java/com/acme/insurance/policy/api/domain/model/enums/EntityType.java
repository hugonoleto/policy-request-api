package com.acme.insurance.policy.api.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityType {

    PAYMENT("Pagamento"),
    BOTH("Ambos"),
    SUBSCRIPTION("Assinatura");

    private final String description;

}
