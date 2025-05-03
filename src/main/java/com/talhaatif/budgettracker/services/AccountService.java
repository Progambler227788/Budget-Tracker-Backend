package com.talhaatif.budgettracker.services;


import com.talhaatif.budgettracker.entities.Account;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);
    Optional<Account> getAccountById(String id);
    List<Account> getAllAccounts();
    Account updateAccount(Account account);
    void deleteAccount(String id);

    String getAccountIdByName(String userId, String accountName);
    String getFirstAccountIdForUser(String userId);

}

