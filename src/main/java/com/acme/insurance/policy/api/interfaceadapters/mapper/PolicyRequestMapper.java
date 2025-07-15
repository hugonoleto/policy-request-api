package com.acme.insurance.policy.api.interfaceadapters.mapper;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestAssistance;
import com.acme.insurance.policy.api.domain.model.PolicyRequestAssistanceId;
import com.acme.insurance.policy.api.domain.model.PolicyRequestCoverage;
import com.acme.insurance.policy.api.domain.model.PolicyRequestCoverageId;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreateDTO;
import com.acme.insurance.policy.api.interfaceadapters.dto.PolicyRequestCreatedResponseDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface PolicyRequestMapper {

    PolicyRequestMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(PolicyRequestMapper.class);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "finishedAt", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "status", constant = "RECEIVED")
    @Mapping(target = "coverages", source = "coverages", qualifiedByName = "mapCoverages")
    @Mapping(target = "assistances", source = "assistances", qualifiedByName = "mapAssistances")
    PolicyRequest toEntity(PolicyRequestCreateDTO dto);

    PolicyRequestCreatedResponseDTO toResponse(PolicyRequest policyRequest);

    @Named("mapCoverages")
    default Set<PolicyRequestCoverage> mapCoverages(Map<String, java.math.BigDecimal> coverages) {
        if (coverages == null) return Collections.emptySet();
        return coverages.entrySet().stream()
                .map(e -> PolicyRequestCoverage.builder()
                        .id(new PolicyRequestCoverageId(null, e.getKey()))
                        .amount(e.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("mapAssistances")
    default Set<PolicyRequestAssistance> mapAssistances(List<String> assistances) {
        if (assistances == null) return Collections.emptySet();
        return assistances.stream()
                .map(name -> PolicyRequestAssistance.builder()
                        .id(new PolicyRequestAssistanceId(null, name))
                        .build())
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void setRelations(@MappingTarget PolicyRequest entity) {
        entity.init();
        if (entity.getAssistances() != null) {
            entity.getAssistances().forEach(a -> {
                a.setPolicyRequest(entity);
                if (a.getId() != null) {
                    a.getId().setPolicyRequestId(entity.getId());
                }
            });
        }
        if (entity.getCoverages() != null) {
            entity.getCoverages().forEach(c -> {
                c.setPolicyRequest(entity);
                if (c.getId() != null) {
                    c.getId().setPolicyRequestId(entity.getId());
                }
            });
        }
    }
}