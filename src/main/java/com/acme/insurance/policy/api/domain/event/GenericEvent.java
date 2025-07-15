package com.acme.insurance.policy.api.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID policyRequestId;

}