package com.shantanu.FinPilot.ai.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiFinancialContext {

    private Double monthlyIncome;

    private Double totalExpenses;

    private Double monthlySavings;

    private Double savingsRate;

    private RiskProfile riskProfile;

    private List<String> recommendations;


}