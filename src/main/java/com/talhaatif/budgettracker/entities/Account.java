package com.talhaatif.budgettracker.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Account extends BaseEntity {

    @Column(nullable = false)
    private String accountName;

    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalIncome;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalExpense;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    public enum AccountType {
        CASH, BANK_ACCOUNT, CREDIT_CARD, INVESTMENT, LOAN, OTHER
    }

    public void updateAccountAggregates(Transaction transaction) {
        if (transaction.getType() == Transaction.TransactionType.INCOME) {
            this.totalIncome = this.totalIncome.add(transaction.getAmount());
        } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            this.totalExpense = this.totalExpense.add(transaction.getAmount());
        }
        this.balance = this.totalIncome.subtract(this.totalExpense);
    }
}