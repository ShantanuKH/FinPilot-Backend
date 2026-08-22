package com.shantanu.FinPilot.ai.dto.chat;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

}
