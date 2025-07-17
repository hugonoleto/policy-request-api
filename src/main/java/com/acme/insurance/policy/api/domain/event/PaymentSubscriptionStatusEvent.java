package com.acme.insurance.policy.api.domain.event;

import com.acme.insurance.policy.api.domain.model.enums.EntityType;
import com.acme.insurance.policy.api.domain.model.enums.PaymentSubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubscriptionStatusEvent extends GenericEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PaymentSubscriptionStatus paymentSubscriptionStatus;
    private EntityType entityType;

}