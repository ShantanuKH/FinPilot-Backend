package com.shantanu.FinPilot.expense.controller;


import com.shantanu.FinPilot.expense.dto.CreateExpenseRequest;
import com.shantanu.FinPilot.expense.dto.ExpenseResponse;
import com.shantanu.FinPilot.expense.dto.UpdateExpenseRequest;
import com.shantanu.FinPilot.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.shantanu.FinPilot.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;

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

    //    Get Expenses
    @GetMapping
    public PagedResponse<ExpenseResponse> getMyExpenses(
            Authentication authentication,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ExpenseCategory category,
            @PageableDefault(
                    sort = "expenseDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        String email = authentication.getName();

        return expenseService.getMyExpenses(
                email,
                search,
                category,
                pageable
        );
    }

//    Delete Expenses
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String email = authentication.getName();

        expenseService.deleteExpense(id, email);

        return ResponseEntity.ok(
                "Expense deleted successfully"
        );



}

    //        Update the expense
    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody UpdateExpenseRequest updateExpenseRequest
    ) {

        String email = authentication.getName();

        return expenseService.updateExpense(
                id,
                email,
                updateExpenseRequest
        );
    }
}