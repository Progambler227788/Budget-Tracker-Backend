package com.talhaatif.budgettracker;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@OpenAPIDefinition
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class BudgetTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetTrackerApplication.class, args);
	}

}
