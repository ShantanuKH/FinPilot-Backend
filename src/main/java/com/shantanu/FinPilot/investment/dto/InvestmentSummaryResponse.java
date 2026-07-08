package com.shantanu.FinPilot.investment.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentSummaryResponse {
    private Double totalInvestment;

    private Long investmentCount;

    private Double largestInvestment;

    private Double averageInvestment;
}
