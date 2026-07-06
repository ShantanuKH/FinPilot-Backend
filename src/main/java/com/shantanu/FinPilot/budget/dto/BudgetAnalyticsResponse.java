package com.shantanu.FinPilot.budget.dto;

import com.shantanu.FinPilot.budget.entity.BudgetStatus;
import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAnalyticsResponse {

    private Long budgetId;

    private ExpenseCategory category;

    private Double budgetAmount;

    private Double spentAmount;

    private Double remainingAmount;

    private Double usagePercentage;

    private BudgetStatus status;
}
