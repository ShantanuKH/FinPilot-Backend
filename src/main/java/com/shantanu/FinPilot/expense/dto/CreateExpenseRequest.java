package com.shantanu.FinPilot.expense.dto;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateExpenseRequest {

    private String title;

    private Double amount;

    private String description;

    private LocalDate expenseDate;

    private ExpenseCategory category;
}
