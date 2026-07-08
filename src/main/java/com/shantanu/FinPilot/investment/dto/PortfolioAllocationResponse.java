package com.shantanu.FinPilot.investment.dto;

import com.shantanu.FinPilot.investment.entity.InvestmentType;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioAllocationResponse {

    private InvestmentType investmentType;

    private Double amount;

    private Double percentage;
}
