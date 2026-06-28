package com.shantanu.FinPilot.user.service;

import com.shantanu.FinPilot.user.dto.UserProfileResponse;
import com.shantanu.FinPilot.user.entity.User;
import com.shantanu.FinPilot.user.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getCurrentUserProfile(
            String email
    ) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User Not Found"
                        )
                );

        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .monthlyIncome(user.getMonthlyIncomne())
                .riskProfile(user.getRiskProfile())
                .currency(user.getCurrency())
                .build();
    }

}
