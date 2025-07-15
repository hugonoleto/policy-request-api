package com.acme.insurance.policy.api.domain.model;

import lombok.Data;

@Data
public class Occurrence {

    private String id;
    private int productId;
    private String type;
    private String description;
    private String createdAt;
    private String updatedAt;

}
