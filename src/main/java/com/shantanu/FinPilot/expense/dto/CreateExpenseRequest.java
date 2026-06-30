package com.shantanu.FinPilot.expense.dto;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateExpenseRequest {

    @NotBlank
    private String title;

    @NotNull
    @Positive
    private Double amount;

    private String description;

    @NotNull
    private LocalDate expenseDate;

    @NotNull
    private ExpenseCategory category;
}
