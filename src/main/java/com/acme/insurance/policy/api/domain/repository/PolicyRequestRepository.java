package com.acme.insurance.policy.api.domain.repository;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRequestRepository extends JpaRepository<PolicyRequest, UUID>, JpaSpecificationExecutor<PolicyRequest> {

    @Query("SELECT p FROM PolicyRequest p WHERE p.id = :id AND p.status NOT IN :excludedStatuses")
    Optional<PolicyRequest> findByIdAndStatusNotIn(@Param("id") UUID id, @Param("excludedStatuses") List<String> excludedStatuses);

}