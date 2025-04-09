package com.talhaatif.budgettracker.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private BigDecimal totalPlanned;  // Total budget amount

    @Column(nullable = false)
    private BigDecimal totalIncome;   // Aggregated income

    @Column(nullable = false)
    private BigDecimal totalExpense;  // Aggregated expense

    @Column(nullable = false)
    private BigDecimal currentBalance; // Calculated: totalIncome - totalExpense

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BudgetCategory> categories;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    // Helper method to update aggregates
    public void updateAggregates(Transaction transaction) {
        if (transaction.getType() == Transaction.TransactionType.INCOME) {
            this.totalIncome = this.totalIncome.add(transaction.getAmount());
        } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            this.totalExpense = this.totalExpense.add(transaction.getAmount());
        }
        this.currentBalance = this.totalIncome.subtract(this.totalExpense);
    }
}