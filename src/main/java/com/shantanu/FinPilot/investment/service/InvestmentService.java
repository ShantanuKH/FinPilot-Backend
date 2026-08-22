package com.shantanu.FinPilot.investment.service;

import com.shantanu.FinPilot.common.exception.InvestmentNotFoundException;
import com.shantanu.FinPilot.common.exception.UnauthorizedInvestmentAccessException;
import com.shantanu.FinPilot.common.exception.UserNotFoundException;
import com.shantanu.FinPilot.investment.dto.*;
import com.shantanu.FinPilot.investment.entity.Investment;
import com.shantanu.FinPilot.investment.entity.InvestmentType;
import com.shantanu.FinPilot.investment.repository.InvestmentRepository;
import com.shantanu.FinPilot.user.entity.RiskProfile;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final UserRepository userRepository;
    private final InvestmentRepository investmentRepository;

    // =========================================================
    // Helper Methods
    // =========================================================

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User Not Found"
                        ));
    }

    private List<Investment> getUserInvestments(User user) {
        List<Investment> investments =
                investmentRepository.findByUser(user);

        return investments != null
                ? investments
                : Collections.emptyList();
    }

    private Investment getInvestmentById(Long investmentId) {
        return investmentRepository.findById(investmentId)
                .orElseThrow(() ->
                        new InvestmentNotFoundException(
                                "Investment Not Found"
                        ));
    }

    private void validateInvestmentOwnership(
            User currentUser,
            Investment investment
    ) {
        if (investment.getUser() == null
                || !investment.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new UnauthorizedInvestmentAccessException(
                    "You are not authorized to access this investment"
            );
        }
    }

    // =========================================================
    // Create Investment
    // =========================================================

    public InvestmentResponse createInvestment(
            String email,
            CreateInvestmentRequest createInvestmentRequest
    ) {

        User user = getUserByEmail(email);

        Investment investment =
                Investment.builder()
                        .name(
                                createInvestmentRequest.getName()
                        )
                        .amount(
                                createInvestmentRequest.getAmount()
                        )
                        .investmentType(
                                createInvestmentRequest
                                        .getInvestmentType()
                        )
                        .investmentDate(
                                createInvestmentRequest
                                        .getInvestmentDate()
                        )
                        .user(user)
                        .build();

        Investment savedInvestment =
                investmentRepository.save(investment);

        return InvestmentResponse.builder()
                .id(savedInvestment.getId())
                .name(savedInvestment.getName())
                .amount(savedInvestment.getAmount())
                .investmentType(
                        savedInvestment.getInvestmentType()
                )
                .investmentDate(
                        savedInvestment.getInvestmentDate()
                )
                .build();
    }

    // =========================================================
    // Get My Investments
    // =========================================================

    public List<InvestmentResponse> getMyInvestments(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Investment> investments =
                getUserInvestments(user);

        return investments.stream()
                .map(investment ->
                        InvestmentResponse.builder()
                                .id(investment.getId())
                                .name(investment.getName())
                                .amount(investment.getAmount())
                                .investmentType(
                                        investment.getInvestmentType()
                                )
                                .investmentDate(
                                        investment.getInvestmentDate()
                                )
                                .build()
                )
                .toList();
    }

    // =========================================================
    // Update Investment
    // =========================================================

    public InvestmentResponse updateInvestment(
            Long investmentId,
            String email,
            UpdateInvestmentRequest request
    ) {

        User currentUser = getUserByEmail(email);

        Investment investment =
                getInvestmentById(investmentId);

        validateInvestmentOwnership(
                currentUser,
                investment
        );

        investment.setName(
                request.getName()
        );

        investment.setAmount(
                request.getAmount()
        );

        investment.setInvestmentType(
                request.getInvestmentType()
        );

        investment.setInvestmentDate(
                request.getInvestmentDate()
        );

        Investment updatedInvestment =
                investmentRepository.save(investment);

        return InvestmentResponse.builder()
                .id(updatedInvestment.getId())
                .name(updatedInvestment.getName())
                .amount(updatedInvestment.getAmount())
                .investmentType(
                        updatedInvestment.getInvestmentType()
                )
                .investmentDate(
                        updatedInvestment.getInvestmentDate()
                )
                .build();
    }

    // =========================================================
    // Delete Investment
    // =========================================================

    public void deleteInvestment(
            Long investmentId,
            String email
    ) {

        User currentUser = getUserByEmail(email);

        Investment investment =
                getInvestmentById(investmentId);

        validateInvestmentOwnership(
                currentUser,
                investment
        );

        investmentRepository.delete(investment);
    }

    // =========================================================
    // Investment Summary
    // =========================================================

    public InvestmentSummaryResponse getInvestmentSummary(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Investment> investments =
                getUserInvestments(user);

        double totalInvestment =
                investments.stream()
                        .filter(investment ->
                                investment.getAmount() != null
                        )
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        long investmentCount =
                investments.size();

        double largestInvestment =
                investments.stream()
                        .filter(investment ->
                                investment.getAmount() != null
                        )
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .max()
                        .orElse(0.0);

        double averageInvestment =
                investments.stream()
                        .filter(investment ->
                                investment.getAmount() != null
                        )
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .average()
                        .orElse(0.0);

        return InvestmentSummaryResponse.builder()
                .totalInvestment(totalInvestment)
                .investmentCount(investmentCount)
                .largestInvestment(largestInvestment)
                .averageInvestment(averageInvestment)
                .build();
    }

    // =========================================================
    // Portfolio Allocation
    // =========================================================

    public List<PortfolioAllocationResponse> getPortfolioAllocation(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Investment> investments =
                getUserInvestments(user);

        double totalInvestment =
                investments.stream()
                        .filter(investment ->
                                investment.getAmount() != null
                        )
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        Map<InvestmentType, Double> allocationMap =
                investments.stream()
                        .filter(investment ->
                                investment.getInvestmentType() != null
                        )
                        .filter(investment ->
                                investment.getAmount() != null
                        )
                        .collect(
                                Collectors.groupingBy(
                                        Investment::getInvestmentType,
                                        Collectors.summingDouble(
                                                Investment::getAmount
                                        )
                                )
                        );

        return allocationMap.entrySet()
                .stream()
                .sorted(
                        Map.Entry
                                .<InvestmentType, Double>
                                        comparingByValue()
                                .reversed()
                )
                .map(entry -> {

                    double percentage =
                            totalInvestment > 0
                                    ? (
                                    entry.getValue()
                                            / totalInvestment
                            ) * 100
                                    : 0.0;

                    percentage =
                            Math.round(
                                    percentage * 100.0
                            ) / 100.0;

                    return PortfolioAllocationResponse
                            .builder()
                            .investmentType(
                                    entry.getKey()
                            )
                            .amount(
                                    entry.getValue()
                            )
                            .percentage(
                                    percentage
                            )
                            .build();
                })
                .toList();
    }

    // =========================================================
    // Risk Analysis
    // =========================================================

    public InvestmentRiskAnalysisResponse getRiskAnalysis(
            String email
    ) {

        User user = getUserByEmail(email);

        List<Investment> investments =
                getUserInvestments(user);

        // -----------------------------------------------------
        // Calculate total investment
        // -----------------------------------------------------

        double totalInvestment =
                investments.stream()
                        .filter(investment ->
                                investment.getAmount() != null
                        )
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        // -----------------------------------------------------
        // Calculate high-risk investments
        //
        // Currently STOCK and CRYPTO are treated as
        // high-risk investments according to the existing
        // FinPilot logic.
        // -----------------------------------------------------

        double highRiskAmount = 0.0;

        for (Investment investment : investments) {

            if (investment.getAmount() == null
                    || investment.getInvestmentType() == null) {
                continue;
            }

            if (investment.getInvestmentType()
                    == InvestmentType.CRYPTO
                    ||
                    investment.getInvestmentType()
                            == InvestmentType.STOCK) {

                highRiskAmount +=
                        investment.getAmount();
            }
        }

        // -----------------------------------------------------
        // Calculate high-risk percentage
        // -----------------------------------------------------

        double highRiskPercentage =
                totalInvestment > 0
                        ? (
                        highRiskAmount
                                / totalInvestment
                ) * 100
                        : 0.0;

        highRiskPercentage =
                Math.round(
                        highRiskPercentage * 100.0
                ) / 100.0;

        // -----------------------------------------------------
        // Determine portfolio risk
        // -----------------------------------------------------

        String portfolioRisk;

        if (totalInvestment == 0) {

            /*
             * New user / user with no investments.
             *
             * We don't have enough portfolio data to
             * determine actual portfolio risk.
             *
             * The response still uses LOW here to remain
             * compatible with the existing DTO/frontend,
             * while the message clearly explains that
             * there is no portfolio to analyse yet.
             */

            portfolioRisk = "LOW";

        } else if (highRiskPercentage > 60) {

            portfolioRisk = "HIGH";

        } else if (highRiskPercentage >= 30) {

            portfolioRisk = "MODERATE";

        } else {

            portfolioRisk = "LOW";
        }

        // -----------------------------------------------------
        // User Risk Profile
        // -----------------------------------------------------

        RiskProfile userRiskProfile =
                user.getRiskProfile();

        String message;

        /*
         * IMPORTANT:
         *
         * A newly registered user may not have selected
         * a risk profile yet.
         *
         * Never call:
         *
         * userRiskProfile.name()
         *
         * without checking for null.
         */

        if (userRiskProfile == null) {

            if (totalInvestment == 0) {

                message =
                        "Welcome to FinPilot Investments. " +
                                "Add your first investment and " +
                                "complete your risk profile to " +
                                "unlock personalized portfolio analysis.";

            } else {

                message =
                        "Your portfolio has been analysed, " +
                                "but you have not selected a risk profile yet. " +
                                "Complete your financial profile to compare " +
                                "your portfolio with your preferred risk level.";
            }

        } else if (userRiskProfile.name()
                .equals(portfolioRisk)) {

            message =
                    "Your portfolio risk matches your " +
                            "selected risk profile.";

        } else {

            message =
                    "Your portfolio risk does not match " +
                            "your selected risk profile.";
        }

        // -----------------------------------------------------
        // Build Response
        // -----------------------------------------------------

        return InvestmentRiskAnalysisResponse
                .builder()
                .userRiskProfile(
                        userRiskProfile
                )
                .portfolioRisk(
                        portfolioRisk
                )
                .message(
                        message
                )
                .build();
    }
}