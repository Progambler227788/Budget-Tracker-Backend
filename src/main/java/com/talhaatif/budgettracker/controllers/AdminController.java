package com.talhaatif.budgettracker.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// @RequestMapping is an annotation in Spring Boot
// that is used to map HTTP requests to specific controller classes or methods.
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") //
@Tag(name = "Admin APIS",description = "Admin related apis")
public class AdminController {





}

