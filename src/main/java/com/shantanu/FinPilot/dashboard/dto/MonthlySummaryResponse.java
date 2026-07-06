package com.shantanu.FinPilot.dashboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySummaryResponse {
    private String month;

    private Double totalAmount;
}
