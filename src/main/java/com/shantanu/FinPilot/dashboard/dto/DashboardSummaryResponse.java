package com.shantanu.FinPilot.dashboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private Double totalExpenses;

    private Long expenseCount;

    private Double highestExpense;

    private Double averageExpense;
}
