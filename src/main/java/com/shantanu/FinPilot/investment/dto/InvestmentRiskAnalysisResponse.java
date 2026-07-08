package com.shantanu.FinPilot.investment.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentRiskAnalysisResponse {

    private RiskProfile userRiskProfile;

    private String portfolioRisk;

    private String message;
}
