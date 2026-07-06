package com.shantanu.FinPilot.budget.controller;

import com.shantanu.FinPilot.budget.dto.BudgetAnalyticsResponse;
import com.shantanu.FinPilot.budget.dto.BudgetResponse;
import com.shantanu.FinPilot.budget.dto.CreateBudgetRequest;
import com.shantanu.FinPilot.budget.dto.UpdateBudgetRequest;
import com.shantanu.FinPilot.budget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public BudgetResponse createBudget(
            @Valid @RequestBody CreateBudgetRequest createBudgetRequest,
            Authentication authentication
            ){
        String email = authentication.getName();

        return budgetService.createBudget(
                email,
                createBudgetRequest
        );

    }

    @GetMapping
    public List<BudgetResponse> getMyBudgets(
            Authentication authentication
    ) {

        return budgetService.getMyBudgets(
                authentication.getName()
        );
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long budgetId,
            @Valid @RequestBody UpdateBudgetRequest updateBudgetRequest,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                budgetService.updateBudget(
                        budgetId,
                        email,
                        updateBudgetRequest
                )
        );
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<String> deleteBudget(
            @PathVariable Long budgetId,
            Authentication authentication
    ) {

        budgetService.deleteBudget(
                budgetId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Budget deleted successfully"
        );
    }

    @GetMapping("/analytics")
    public ResponseEntity<List<BudgetAnalyticsResponse>>
    getBudgetAnalytics(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                budgetService.getBudgetAnalytics(
                        email
                )
        );
    }

}