package com.acme.insurance.policy.api.domain.repository;

import com.acme.insurance.policy.api.domain.model.CustomerBlocked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerBlockedRepository extends JpaRepository<CustomerBlocked, UUID> {

    boolean existsByIdAndEntityTypeIn(UUID id, Iterable<String> entityTypes);

}