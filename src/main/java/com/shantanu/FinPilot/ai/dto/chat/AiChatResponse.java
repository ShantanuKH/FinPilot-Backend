package com.shantanu.FinPilot.ai.dto.chat;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {

    @NotBlank(message = "Message cannot be empty")
    private String answer;

}