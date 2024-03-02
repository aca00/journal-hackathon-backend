package com.hack.journal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@Builder
public class AutoEmojiCountMetric {
    private String emojiAuto;
    private Integer emojiCount;

    public AutoEmojiCountMetric(String emojiAuto, Integer emojiCount) {
        this.emojiAuto = emojiAuto;
        this.emojiCount = emojiCount;
    }
}
