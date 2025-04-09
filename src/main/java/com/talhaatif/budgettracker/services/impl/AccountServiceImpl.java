package com.talhaatif.budgettracker.services.impl;


import com.talhaatif.budgettracker.entities.Account;
import com.talhaatif.budgettracker.repositories.AccountRepository;
import com.talhaatif.budgettracker.services.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;

    public AccountServiceImpl(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public Account createAccount(Account account) {
        return accountRepo.save(account);
    }

    @Override
    public Optional<Account> getAccountById(String id) {
        return accountRepo.findById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepo.save(account);
    }

    @Override
    public void deleteAccount(String id) {
        accountRepo.deleteById(id);
    }
}

