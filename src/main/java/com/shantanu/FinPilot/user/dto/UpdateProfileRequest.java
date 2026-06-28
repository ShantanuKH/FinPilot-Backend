package com.shantanu.FinPilot.user.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @NotNull
    @Positive
    private Double monthlyIncome;

    @NotNull
    private RiskProfile riskProfile;

    @NotBlank
    private String currency;
}
