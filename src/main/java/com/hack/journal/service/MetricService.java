package com.hack.journal.service;

import com.hack.journal.dto.AutoEmojiCountMetric;
import com.hack.journal.dto.AutoEmojiMetricAnalysisDto;
import com.hack.journal.repository.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetricService {
    @Autowired
    MetricRepository metricRepository;

    @Autowired
    GeminiService geminiService;

    public AutoEmojiMetricAnalysisDto analyseEmojiReactions(long userId, Timestamp start, Timestamp end) {
        if (start == null || end == null) {
            throw new RuntimeException("Timestamps can't be null");
        }

        AutoEmojiMetricAnalysisDto autoEmojiMetricAnalysisDto = new AutoEmojiMetricAnalysisDto();
        Map<String, Double> emojiMap = new HashMap<>();

        List<AutoEmojiCountMetric> metrics = metricRepository.getListOfAutoEmojiCountMetric(userId, start, end);

        int sum = metrics.stream().map(AutoEmojiCountMetric::getEmojiCount)
                .reduce(Integer::sum).orElse(0);

        if (sum == 0) {
            throw new RuntimeException("Analysis not available");
        }

        StringBuilder stringBuilder = new StringBuilder("[");

        for (AutoEmojiCountMetric metric : metrics) {
            double d =  ((double)metric.getEmojiCount() / sum) * 100;
            stringBuilder.append(metric.getEmojiAuto());
            stringBuilder.append(" : ");
            stringBuilder.append(metric.getEmojiCount());
            stringBuilder.append("%; ");
            emojiMap.put(metric.getEmojiAuto(), d);
        }
        stringBuilder.append("]");

        autoEmojiMetricAnalysisDto.setEmojiMap(emojiMap);
        String message = geminiService.getEmojiAnalysisMessage(Integer.toString(sum), stringBuilder.toString());
        autoEmojiMetricAnalysisDto.setMessage(message);
        return autoEmojiMetricAnalysisDto;
    }
}
