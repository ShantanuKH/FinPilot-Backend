package com.shantanu.FinPilot.budget.dto;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBudgetRequest {

    @NotNull(message = "Category is required")
    private ExpenseCategory category;

    @NotNull(message = "Budget amount is required")
    @Positive(message = "Budget amount must be greater than 0")
    private Double budgetAmount;

    @NotBlank(message = "Month is required")
    private String month;
}