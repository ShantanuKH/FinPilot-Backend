package com.shantanu.FinPilot.profile.dto;


import com.shantanu.FinPilot.profile.entity.FinancialHealthStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialHealthResponse {

    private Double monthlyIncome;

    private Double totalExpenses;

    private Double monthlySavings;

    private Double savingsRate;

    private FinancialHealthStatus status;
}
