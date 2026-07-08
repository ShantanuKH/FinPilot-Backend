package com.shantanu.FinPilot.investment.controller;

import com.shantanu.FinPilot.investment.dto.*;
import com.shantanu.FinPilot.investment.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<InvestmentResponse> createInvestment(
            Authentication authentication,
            @Valid @RequestBody CreateInvestmentRequest createInvestmentRequest
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                investmentService.createInvestment(
                        email,
                        createInvestmentRequest
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<InvestmentResponse>> getMyInvestments(
            Authentication authentication
    ){
        String email = authentication.getName();

        return ResponseEntity.ok(
                investmentService.getMyInvestments(email)
        );
    }

    @PutMapping("/{investmentId}")
    public ResponseEntity<InvestmentResponse> updateInvestments(
            Authentication authentication,
            @PathVariable Long investmentId,
            @Valid @RequestBody UpdateInvestmentRequest updateInvestmentRequest

            ){
        String email = authentication.getName();

        return ResponseEntity.ok(
                investmentService.updateInvestment(
                        investmentId,
                        email,
                        updateInvestmentRequest)
        );
    }

    @DeleteMapping("/{investmentId}")
    public ResponseEntity<String> deleteInvestment(
            @PathVariable Long investmentId,
            Authentication authentication
    ){
        String email =
                authentication.getName();

        investmentService.deleteInvestment(
                investmentId,
                email
        );

        return ResponseEntity.ok(
                "Investment deleted successfully"
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<InvestmentSummaryResponse>
    getInvestmentSummary(
            Authentication authentication
    ){
        String email =
                authentication.getName();

        return ResponseEntity.ok(
                investmentService.getInvestmentSummary(
                        email
                )
        );

    }

    @GetMapping("/allocation")
    public ResponseEntity<List<PortfolioAllocationResponse>> getPortfolioAllocation(
            Authentication authentication
    ){

        String email = authentication.getName();

        return ResponseEntity.ok(
                investmentService.getPortfolioAllocation(
                        email
                )
        );
    }

    @GetMapping("/risk-analysis")
    public ResponseEntity<InvestmentRiskAnalysisResponse>
    getRiskAnalysis(
            Authentication authentication
    ){
        String email =
                authentication.getName();

        return ResponseEntity.ok(
                investmentService.getRiskAnalysis(
                        email
                )
        );
    }
}