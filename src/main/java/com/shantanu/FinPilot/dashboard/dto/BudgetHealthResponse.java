package com.shantanu.FinPilot.dashboard.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetHealthResponse {
    private Double monthlyIncome;

    private Double totalExpenses;

    private Double remainingAmount;

    private Double savingRate;
}
