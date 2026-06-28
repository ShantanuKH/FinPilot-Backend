package com.shantanu.FinPilot.user.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;

public class UpdateProfileRequest {
    private String firstName;

    private String lastName;

    private Double monthlyIncome;

    private RiskProfile riskProfile;

    private String currency;
}
