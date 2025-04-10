package com.talhaatif.budgettracker.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal balance;



    @Column(nullable = false)
    private BigDecimal totalIncome;   // income

    @Column(nullable = false)
    private BigDecimal totalExpense;  // expense

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


    // Helper method to update aggregates
    public void updateAccountAggregates(Transaction transaction) {
        if (transaction.getType() == Transaction.TransactionType.INCOME) {
            this.totalIncome = this.totalIncome.add(transaction.getAmount());
        } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            this.totalExpense = this.totalExpense.add(transaction.getAmount());
        }
        this.balance = this.totalIncome.subtract(this.totalExpense);
    }
}
