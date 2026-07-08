package com.shantanu.FinPilot.investment.dto;

import com.shantanu.FinPilot.investment.entity.InvestmentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentResponse {

    private Long id;

    private String name;

    private Double amount;

    private InvestmentType investmentType;

    private LocalDate investmentDate;
}