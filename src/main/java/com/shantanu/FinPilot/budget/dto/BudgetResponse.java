package com.shantanu.FinPilot.budget.dto;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {

    private Long id;

    private ExpenseCategory category;

    private Double budgetAmount;

    private String month;
}
