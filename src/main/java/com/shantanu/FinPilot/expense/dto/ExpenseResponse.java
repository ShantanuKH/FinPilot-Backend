package com.shantanu.FinPilot.expense.dto;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private Long id;

    private String title;

    private Double amount;

    private String description;

    private LocalDate expenseDate;

    private ExpenseCategory category;
}