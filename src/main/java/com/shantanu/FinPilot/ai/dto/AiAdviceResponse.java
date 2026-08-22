package com.shantanu.FinPilot.ai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAdviceResponse {

    private String summary;

    private List<String> strengths;

    private List<String> improvements;

    private List<String> actionItems;

    private String motivation;

}