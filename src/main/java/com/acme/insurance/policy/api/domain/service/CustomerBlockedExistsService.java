package com.acme.insurance.policy.api.domain.service;

import com.acme.insurance.policy.api.domain.model.enums.EntityType;
import com.acme.insurance.policy.api.domain.repository.CustomerBlockedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.acme.insurance.policy.api.domain.model.enums.EntityType.BOTH;

@Service
@RequiredArgsConstructor
public class CustomerBlockedExistsService {

    private final CustomerBlockedRepository customerBlockedRepository;

    public boolean exists(UUID customerId, EntityType entityType) {
        return customerBlockedRepository.existsByIdAndEntityTypeIn(customerId, List.of(BOTH.name(), entityType.name()));
    }
}
