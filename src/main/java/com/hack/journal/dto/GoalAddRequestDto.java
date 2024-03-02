package com.hack.journal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalAddRequestDto {
    private boolean isNLPrompt;
    private String nlPrompt;
    private String title;
    private String description;
    private Timestamp start;
    private int numberOfDays;
}
