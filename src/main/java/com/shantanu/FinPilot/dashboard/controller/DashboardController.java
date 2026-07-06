package com.shantanu.FinPilot.dashboard.controller;

import com.shantanu.FinPilot.dashboard.dto.BudgetHealthResponse;
import com.shantanu.FinPilot.dashboard.dto.CategoryBreakdownResponse;
import com.shantanu.FinPilot.dashboard.dto.DashboardSummaryResponse;
import com.shantanu.FinPilot.dashboard.dto.MonthlySummaryResponse;
import com.shantanu.FinPilot.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getDashboardSummary(
            Authentication authentication
    ){
        String email = authentication.getName();

        return dashboardService.getDashboardSummary(
                email
        );
    }

    @GetMapping("/category-breakdown")
    public List<CategoryBreakdownResponse>
    getCategoryBreakdown(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return dashboardService
                .getCategoryBreakdown(email);
    }

    @GetMapping("/monthly-summary")
    public List<MonthlySummaryResponse> getMonthlySummary(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return dashboardService.getMonthlySummary(email);
    }

    @GetMapping("/budget-health")
    public BudgetHealthResponse getBudgetHealth(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return dashboardService
                .getBudgetHealth(email);
    }

}