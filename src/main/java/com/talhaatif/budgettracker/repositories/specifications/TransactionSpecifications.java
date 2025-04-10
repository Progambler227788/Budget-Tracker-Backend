package com.talhaatif.budgettracker.repositories.specifications;

import com.talhaatif.budgettracker.entities.Transaction;
import com.talhaatif.budgettracker.entities.TransactionSearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionSpecifications {

    public static Specification<Transaction> withCriteria(TransactionSearchCriteria criteria) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            // Amount range filter
            if (criteria.getMinAmount() != null) {
                predicate = builder.and(predicate,
                        builder.greaterThanOrEqualTo(root.get("amount"), criteria.getMinAmount()));
            }
            if (criteria.getMaxAmount() != null) {
                predicate = builder.and(predicate,
                        builder.lessThanOrEqualTo(root.get("amount"), criteria.getMaxAmount()));
            }

            // Date range filter
            if (criteria.getStartDate() != null) {
                predicate = builder.and(predicate,
                        builder.greaterThanOrEqualTo(root.get("date"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicate = builder.and(predicate,
                        builder.lessThanOrEqualTo(root.get("date"), criteria.getEndDate()));
            }

            // Description filter (case insensitive contains)
            if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
                predicate = builder.and(predicate,
                        builder.like(builder.lower(root.get("description")),
                                "%" + criteria.getDescription().toLowerCase() + "%"));
            }

            // Transaction type filter
            if (criteria.getType() != null) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("type"), criteria.getType()));
            }

            // Relationship filters
            if (criteria.getAccountId() != null && !criteria.getAccountId().isEmpty()) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("account").get("id"), criteria.getAccountId()));
            }
            if (criteria.getCategoryId() != null && !criteria.getCategoryId().isEmpty()) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("category").get("id"), criteria.getCategoryId()));
            }
            if (criteria.getUserId() != null && !criteria.getUserId().isEmpty()) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("user").get("id"), criteria.getUserId()));
            }
            if (criteria.getBudgetId() != null && !criteria.getBudgetId().isEmpty()) {
                predicate = builder.and(predicate,
                        builder.equal(root.get("budget").get("id"), criteria.getBudgetId()));
            }

            return predicate;
        };
    }
}
