package com.shantanu.FinPilot.user.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String firstName;

    private String lastName;

    private Double monthlyIncome;

    private RiskProfile riskProfile;

    private String currency;
}
