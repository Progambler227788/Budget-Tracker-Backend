package com.talhaatif.budgettracker.services.validation;


import com.talhaatif.budgettracker.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public boolean isValid(User user) {
        return user.getEmail() != null && user.getPassword() != null;
    }
}
