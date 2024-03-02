package com.hack.journal.controller;

import com.hack.journal.dto.AutoEmojiMetricAnalysisDto;
import com.hack.journal.entity.User;
import com.hack.journal.service.MetricService;
import com.hack.journal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@CrossOrigin
@RequestMapping("api/v1/metrics")
public class MetricAnalysisController {
    @Autowired
    UserService userService;
    @Autowired
    MetricService metricService;

    @GetMapping
    public AutoEmojiMetricAnalysisDto doAnalysis(@RequestParam String start, @RequestParam String end) {


        User user = userService.getCurrentUser();
        return metricService.analyseEmojiReactions(user.getId(), toTimestamp(start), toTimestamp(end));
    }

    private Timestamp toTimestamp(String isoDate) {
        Timestamp timestamp = null;
        try {
            Instant instant = Instant.parse(isoDate);
            timestamp = Timestamp.from(instant);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return timestamp;
    }
}
