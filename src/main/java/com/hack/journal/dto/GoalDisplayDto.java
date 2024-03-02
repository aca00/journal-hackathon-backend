package com.hack.journal.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalDisplayDto {
    private Long id;
    private String title;
    private String description;
    private Timestamp start;
    private Timestamp end;
    private String question;
    private int done;
    private Long userId;
    private boolean doneToday;
    private Double progress;
}
