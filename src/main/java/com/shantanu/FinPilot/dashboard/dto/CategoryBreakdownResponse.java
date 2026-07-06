package com.shantanu.FinPilot.dashboard.dto;

import com.shantanu.FinPilot.expense.entity.ExpenseCategory;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryBreakdownResponse {

       private ExpenseCategory category;

        private Double totalAmount;
}
