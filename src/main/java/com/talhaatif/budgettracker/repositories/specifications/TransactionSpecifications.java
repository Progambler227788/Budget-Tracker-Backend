package com.talhaatif.budgettracker.repositories.specifications;

import com.talhaatif.budgettracker.entities.Transaction;
import com.talhaatif.budgettracker.entities.TransactionSearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionSpecifications {

    // Private constructor to prevent instantiation
    private TransactionSpecifications() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }



    public static Specification<Transaction> withCriteria(TransactionSearchCriteria criteria) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            predicate = applyAmountFilters(predicate, root, builder, criteria);
            predicate = applyDateFilters(predicate, root, builder, criteria);
            predicate = applyDescriptionFilter(predicate, root, builder, criteria);
            predicate = applyTypeFilter(predicate, root, builder, criteria);
            predicate = applyRelationshipFilters(predicate, root, builder, criteria);

            return predicate;
        };
    }

    private static Predicate applyAmountFilters(Predicate predicate, Root<Transaction> root,
                                                CriteriaBuilder builder, TransactionSearchCriteria criteria) {
        if (criteria.getMinAmount() != null) {
            predicate = builder.and(predicate,
                    builder.greaterThanOrEqualTo(root.get("amount"), criteria.getMinAmount()));
        }
        if (criteria.getMaxAmount() != null) {
            predicate = builder.and(predicate,
                    builder.lessThanOrEqualTo(root.get("amount"), criteria.getMaxAmount()));
        }
        return predicate;
    }

    private static Predicate applyDateFilters(Predicate predicate, Root<Transaction> root,
                                              CriteriaBuilder builder, TransactionSearchCriteria criteria) {
        if (criteria.getStartDate() != null) {
            predicate = builder.and(predicate,
                    builder.greaterThanOrEqualTo(root.get("date"), criteria.getStartDate()));
        }
        if (criteria.getEndDate() != null) {
            predicate = builder.and(predicate,
                    builder.lessThanOrEqualTo(root.get("date"), criteria.getEndDate()));
        }
        return predicate;
    }

    private static Predicate applyDescriptionFilter(Predicate predicate, Root<Transaction> root,
                                                    CriteriaBuilder builder, TransactionSearchCriteria criteria) {
        if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
            predicate = builder.and(predicate,
                    builder.like(builder.lower(root.get("description")),
                            "%" + criteria.getDescription().toLowerCase() + "%"));
        }
        return predicate;
    }

    private static Predicate applyTypeFilter(Predicate predicate, Root<Transaction> root,
                                             CriteriaBuilder builder, TransactionSearchCriteria criteria) {
        if (criteria.getType() != null) {
            predicate = builder.and(predicate,
                    builder.equal(root.get("type"), criteria.getType()));
        }
        return predicate;
    }

    private static Predicate applyRelationshipFilters(Predicate predicate, Root<Transaction> root,
                                                      CriteriaBuilder builder, TransactionSearchCriteria criteria) {
        predicate = applyIdFilter(predicate, root, builder, "account", criteria.getAccountId());
        predicate = applyIdFilter(predicate, root, builder, "category", criteria.getCategoryId());
        predicate = applyIdFilter(predicate, root, builder, "user", criteria.getUserId());
        predicate = applyIdFilter(predicate, root, builder, "budget", criteria.getBudgetId());
        return predicate;
    }

    private static Predicate applyIdFilter(Predicate predicate, Root<Transaction> root,
                                           CriteriaBuilder builder, String relationship, String id) {
        if (id != null && !id.isEmpty()) {
            predicate = builder.and(predicate,
                    builder.equal(root.get(relationship).get("id"), id));
        }
        return predicate;
    }
}