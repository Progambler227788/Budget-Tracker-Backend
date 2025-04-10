package com.talhaatif.budgettracker.constants;

public final class ErrorCodes {

    private ErrorCodes() {
        // Prevent instantiation
    }
    public static final String ERROR = "error";
    public static final String INTERNAL_SERVER_ERROR = "500";
    public static final String BAD_REQUEST = "400";
    public static final String NOT_FOUND = "404";
    public static final String UNAUTHORIZED = "401";
    public static final String FORBIDDEN = "403";

    public static final String ACCOUNT_NOT_FOUND = "E001";
    public static final String CUSTOMER_NOT_FOUND = "E002";
    public static final String DATABASE_ERROR = "E003";
    public static final String VALIDATION_FAILED = "E004";
}
