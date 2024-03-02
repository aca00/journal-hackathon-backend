package com.hack.journal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalFromNLDto {
    @JsonProperty("title")
    private String title;
    @JsonProperty("question_remainder")
    private String questionRemainder;
    @JsonProperty("description")
    private String desc;
    @JsonProperty("number_of_days_to_complete_goal")
    private Object numDays;
}
