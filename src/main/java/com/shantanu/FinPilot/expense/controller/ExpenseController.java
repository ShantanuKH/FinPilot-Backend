package com.shantanu.FinPilot.expense.controller;


import com.shantanu.FinPilot.expense.dto.CreateExpenseRequest;
import com.shantanu.FinPilot.expense.dto.ExpenseResponse;
import com.shantanu.FinPilot.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseResponse createExpense(
            Authentication authentication,
            @Valid @RequestBody CreateExpenseRequest request
    ) {

        String email = authentication.getName();

        return expenseService.createExpense(
                email,
                request
        );
    }
}