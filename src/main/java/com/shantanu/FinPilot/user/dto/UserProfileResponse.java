package com.shantanu.FinPilot.user.dto;

import com.shantanu.FinPilot.user.entity.RiskProfile;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String firstName;

    private String lastName;

    private String email;

    private Double monthlyIncome;

    private RiskProfile riskProfile;

    private String currency;
}
