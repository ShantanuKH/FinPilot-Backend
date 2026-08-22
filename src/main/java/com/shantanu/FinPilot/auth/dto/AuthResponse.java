package com.shantanu.FinPilot.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String message;

    private String token;

    private String firstName;

    private String lastName;

    private String email;
}