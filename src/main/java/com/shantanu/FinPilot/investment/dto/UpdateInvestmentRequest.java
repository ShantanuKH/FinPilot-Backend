package com.shantanu.FinPilot.investment.dto;

import com.shantanu.FinPilot.investment.entity.InvestmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInvestmentRequest {


    @NotBlank(message = "Investment name is required")
    private String name;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Investment type is required")
    private InvestmentType investmentType;

    @NotNull(message = "Investment date is required")
    private LocalDate investmentDate;
}
