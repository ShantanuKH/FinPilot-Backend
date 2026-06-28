package com.shantanu.FinPilot.user.service;

import com.shantanu.FinPilot.user.dto.UpdateProfileRequest;
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
                .monthlyIncome(user.getMonthlyIncome())
                .riskProfile(user.getRiskProfile())
                .currency(user.getCurrency())
                .build();
    }



    public UserProfileResponse updateProfile(
            String email,
            UpdateProfileRequest updateProfileRequest
    ){


        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        ()-> new RuntimeException(
                                "User Not Dound"
                        )
                );

//        To update the user profile
        user.setFirstName(updateProfileRequest.getFirstName());

        user.setLastName(updateProfileRequest.getLastName());

        user.setMonthlyIncome(
                updateProfileRequest.getMonthlyIncome()
        );

        user.setRiskProfile(
                updateProfileRequest.getRiskProfile()
        );

        user.setCurrency(
                updateProfileRequest.getCurrency()
        );

        userRepository.save(user);

        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .monthlyIncome(user.getMonthlyIncome())
                .riskProfile(user.getRiskProfile())
                .currency(user.getCurrency())
                .build();
    }

}
