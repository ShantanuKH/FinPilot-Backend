package com.shantanu.FinPilot.profile.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotNull(message = "Monthly income is required")
    @Positive(message = "Monthly income must be greater than 0")
    private Double monthlyIncome;

    @NotNull(message = "Risk profile is required")
    private RiskProfile riskProfile;

    @NotBlank(message = "Currency is required")
    private String currency;
}