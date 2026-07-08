package com.shantanu.FinPilot.investment.service;

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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final UserRepository userRepository;
    private final InvestmentRepository investmentRepository;

    public InvestmentResponse createInvestment(
            String email,
            CreateInvestmentRequest createInvestmentRequest
    ){
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        Investment investment =
                Investment.builder()
                        .name(
                                createInvestmentRequest.getName()
                        )
                        .amount(
                                createInvestmentRequest.getAmount()
                        )
                        .investmentType(
                                createInvestmentRequest.getInvestmentType()
                        )
                        .investmentDate(
                                createInvestmentRequest.getInvestmentDate()
                        )
                        .user(user)
                        .build();

        Investment savedInvestment = investmentRepository.save(investment);

        return InvestmentResponse.builder()
                .id(savedInvestment.getId())
                .name(
                        savedInvestment.getName()
                )
                .amount(
                        savedInvestment.getAmount()
                )
                .investmentType(
                        savedInvestment.getInvestmentType()
                )
                .investmentDate(
                        savedInvestment.getInvestmentDate()
                )
                .build();

    }


    public List<InvestmentResponse> getMyInvestments( String email){

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        List<Investment> investments = investmentRepository.findByUser(user);


        return investments.stream()
                .map(investment ->
                        InvestmentResponse.builder()
                                .id(investment.getId())
                                .name(
                                        investment.getName()
                                )
                                .amount(
                                        investment.getAmount()
                                )
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

    public InvestmentResponse updateInvestment(
            Long investmentId,
            String email,
            UpdateInvestmentRequest request
    ){
        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(
                        ()-> new UserNotFoundException("User Not Found")
                );

        Investment investment =
                investmentRepository.findById(investmentId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Investment Not Found"
                                )
                        );

        if (!investment.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "You are not authorized to update this investment"
            );
        }
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

        Investment updatedInvestment = investmentRepository.save(investment);


        return InvestmentResponse.builder()
                .id(updatedInvestment.getId())
                .name(
                        updatedInvestment.getName()
                )
                .amount(
                        updatedInvestment.getAmount()
                )
                .investmentType(
                        updatedInvestment.getInvestmentType()
                )
                .investmentDate(
                        updatedInvestment.getInvestmentDate()
                )
                .build();

    }

    public void deleteInvestment(
            Long investmentId,
            String email
    ){
        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        Investment investment =
                investmentRepository.findById(
                                investmentId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Investment Not Found"
                                )
                        );

        if (!investment.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "You are not authorized to delete this investment"
            );
        }

        investmentRepository.delete(investment);
    }

    public InvestmentSummaryResponse getInvestmentSummary(
            String email
    ){
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        List<Investment> investments =
                investmentRepository.findByUser(user);

        Double totalInvestment =
                investments.stream()
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        Long investmentCount =
                (long) investments.size();

        Double largestInvestment =
                investments.stream()
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .max()
                        .orElse(0.0);

        Double averageInvestment =
                investments.stream()
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

    public List<PortfolioAllocationResponse> getPortfolioAllocation(
            String email
    ){
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        List<Investment> investments =
                investmentRepository.findByUser(user);

        Double totalInvestment =
                investments.stream()
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        Map<InvestmentType, Double> allocationMap =
                investments.stream()
                        .collect(
                                Collectors.groupingBy(Investment::getInvestmentType, Collectors.summingDouble(Investment::getAmount))
                        );

        return allocationMap.entrySet()
                .stream()
                .map(entry -> {

                    Double percentage =
                            totalInvestment > 0
                                    ? (entry.getValue()
                                    / totalInvestment) * 100
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


    public InvestmentRiskAnalysisResponse getRiskAnalysis(String email)
    {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "User Not Found"
                        )
                );

        List<Investment> investments =
                investmentRepository.findByUser(user);

        Double totalInvestment =
                investments.stream()
                        .mapToDouble(
                                Investment::getAmount
                        )
                        .sum();

        Double highRiskAmount = 0.0;

        for (Investment investment : investments) {

            if (investment.getInvestmentType()
                    == InvestmentType.CRYPTO
                    ||
                    investment.getInvestmentType()
                            == InvestmentType.STOCK) {

                highRiskAmount =
                        highRiskAmount +
                                investment.getAmount();
            }
        }

        Double highRiskPercentage =
                totalInvestment > 0
                        ? (highRiskAmount / totalInvestment) * 100
                        : 0.0;

        String portfolioRisk;

        if (highRiskPercentage > 60) {

            portfolioRisk = "HIGH";

        } else if (highRiskPercentage >= 30) {

            portfolioRisk = "MODERATE";

        } else {

            portfolioRisk = "LOW";
        }

        RiskProfile userRiskProfile = user.getRiskProfile();
        String message;

        if (userRiskProfile.name()
                .equals(portfolioRisk)) {

            message =
                    "Your portfolio risk matches your selected risk profile.";

        } else {

            message =
                    "Your portfolio risk does not match your selected risk profile.";
        }

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
