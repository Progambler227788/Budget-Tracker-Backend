package com.talhaatif.budgettracker.constants;

public final class AccountsConstants {

    private AccountsConstants() {
        // Prevent instantiation
    }

    public static final String DEFAULT_ACCOUNT_TYPE = "Savings";
    public static final String DEFAULT_BRANCH_ADDRESS = "123 Main Street, New York";

    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 = "Account created successfully";

    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200 = "Request processed successfully";

    public static final String STATUS_417 = "417";
    public static final String MESSAGE_417_UPDATE = "Account update failed. Please try again or contact support";
    public static final String MESSAGE_417_DELETE = "Account delete failed. Please try again or contact support";
}