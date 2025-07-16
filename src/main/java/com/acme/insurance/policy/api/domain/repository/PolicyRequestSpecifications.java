package com.acme.insurance.policy.api.domain.repository;

import com.acme.insurance.policy.api.domain.model.PolicyRequest;
import com.acme.insurance.policy.api.domain.model.PolicyRequestFilter;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;
import java.util.stream.Stream;

public class PolicyRequestSpecifications {

    public static Specification<PolicyRequest> filter(PolicyRequestFilter filter) {
        return (root, query, cb) -> {
            var predicates = Stream.of(
                    filter.getCustomerId() != null ? cb.equal(root.get("customerId"), filter.getCustomerId()) : null,
                    filter.getProductId() != null ? cb.equal(root.get("productId"), filter.getProductId()) : null,
                    StringUtils.isNotBlank(filter.getCategory()) ? cb.equal(root.get("category"), filter.getCategory()) : null,
                    StringUtils.isNotBlank(filter.getStatus()) ? cb.equal(root.get("status"), filter.getStatus()) : null,
                    StringUtils.isNotBlank(filter.getSalesChannel()) ? cb.equal(root.get("salesChannel"), filter.getSalesChannel()) : null
            ).filter(Objects::nonNull).toArray(Predicate[]::new);

            return cb.and(predicates);
        };
    }

}
