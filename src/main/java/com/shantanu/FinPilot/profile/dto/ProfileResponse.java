package com.shantanu.FinPilot.profile.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private String firstName;

    private String lastName;

    private String email;

    private Double monthlyIncome;

    private RiskProfile riskProfile;

    private String currency;
}