package com.hack.journal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoEmojiMetricAnalysisDto {
    private Map<String, Double> emojiMap;
    private String message;
}
