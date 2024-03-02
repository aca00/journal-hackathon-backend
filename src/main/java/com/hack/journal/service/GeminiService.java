package com.hack.journal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hack.journal.dto.GoalFromNLDto;
import com.hack.journal.entity.Goal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {
    private final String apiKey = "";
    private static final String GENERATE_CONTENT_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private static final String CONTENT_TYPE = "application/json";

    private static final String GOAL_PROMPT_TEMPLATE = """
            Read the below message within <message> tags and create a plan. A plan must have following fields
            1. title : must be brief.
            2. question_remainder : A question asking whether you did your work to achieve goal today. Example: If goal is about going gym then Did you go to gym today?
            3. description: Simple brief and concise. Should be motivating
            4. number_of_days_to_complete_goal: Give a rough number. If you can't calculate just return null.
                        
            Response should be in json format with the given fields nothing else. Don't return anything else. Just json. If you can\'t give response then just print null.
                        
            Here is the message:
            <message>
            %s
            </message>
            """;

    private static final String SENTIMENT_PROMPT = """
            Analyse the below text within <message> tag and find the sentiment of the user. Sentiment must be one of the following.
             (1). Happy
             (2). Sad
             (3). Angry
             (4). Neutral
            Just give one of the 4 values. Do not return anything else. Default value is Neutral.
            Also suggest a title for the text. These two return values must be delimited by $ without any additional spaces.
            Here is the text
            <message>
            %s
            </message>
            """;

    private static final String EMOJI_ANALYSIS_MESSAGE = """
            Given are the mood swings of a person for the period of %s days. Comment on his mood swings.
            Give suggestions if necessary. Keep it below 100 words. Don't add salutations. Here is the inputs ::: %s
            """;

    private String createData(String prompt) {
        String template = "{\"contents\": [{\"parts\":[{\"text\": \"%s\"}]}]}";
        String data = template.formatted(prompt);
        System.out.println(data);
        return data;
    }

    @SuppressWarnings("unchecked")

    public String getEmojiAnalysisMessage(String days, String emojiSwings) {
        String prompt = EMOJI_ANALYSIS_MESSAGE.formatted(days, emojiSwings).trim().replace("\n", "");
        String out = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Object>> response = objectMapper.readValue((String) send(prompt), Map.class);
            List<Map<String, List<Map<String, Object>>>> candidates = (List<Map<String, List<Map<String, Object>>>>) response.get("candidates");


            Map<String, List<Map<String, Object>>> content = null;
            for (Map<String, List<Map<String, Object>>> candidate : candidates) {
                if (candidate.containsKey("content")) {
                    content = candidate;
                }
            }

            if (content != null) {
                Map<String, List<Map<String, String>>> _content = (Map<String, List<Map<String, String>>>) content.get("content");
                out = _content.get("parts").get(0).get("text");
            } else {
                throw new RuntimeException("Parsing failed");
            }

        } catch (Exception e) {
            log.error("Couldn't create response");
            throw new RuntimeException("Couldn't create response");
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    public String getSentimentAnalysis(String text) {
        String prompt = SENTIMENT_PROMPT.formatted(text).trim().replace("\n", "");
        String out = "";


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Object>> response = objectMapper.readValue((String) send(prompt), Map.class);
            List<Map<String, List<Map<String, Object>>>> candidates = (List<Map<String, List<Map<String, Object>>>>) response.get("candidates");


            Map<String, List<Map<String, Object>>> content = null;
            for (Map<String, List<Map<String, Object>>> candidate : candidates) {
                if (candidate.containsKey("content")) {
                    content = candidate;
                }
            }


            if (content != null) {
                Map<String, List<Map<String, String>>> _content = (Map<String, List<Map<String, String>>>) content.get("content");
                out = _content.get("parts").get(0).get("text");
            } else {
                throw new RuntimeException("Parsing failed");
            }

        } catch (Exception e) {
            log.error("Couldn't create response");
            throw new RuntimeException("Couldn't create response");
        }

        return out;


    }

    @SuppressWarnings("unchecked")
    public Goal createGoalFromNLPrompt(String prompt, Timestamp start) {

        Goal goal = null;
        prompt = GOAL_PROMPT_TEMPLATE.formatted(prompt).trim().replace("\n", "");
        System.out.println(prompt);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Object>> response = objectMapper.readValue((String) send(prompt), Map.class);
            List<Map<String, List<Map<String, Object>>>> candidates = (List<Map<String, List<Map<String, Object>>>>) response.get("candidates");


            Map<String, List<Map<String, Object>>> content = null;
            for (Map<String, List<Map<String, Object>>> candidate : candidates) {
                if (candidate.containsKey("content")) {
                    content = candidate;
                }
            }

            GoalFromNLDto goalFromNLDto = null;

            if (content != null) {
                Map<String, List<Map<String, String>>> _content = (Map<String, List<Map<String, String>>>) content.get("content");
                String text = (String) _content.get("parts").get(0).get("text");
                goalFromNLDto = objectMapper.readValue(_content.get("parts").get(0).get("text").replace("json", ""), GoalFromNLDto.class);
            } else {
                throw new RuntimeException("Parsing failed");
            }


            goal = Goal.builder().build();
            goal.setActive(true);
            goal.setStart(start);
            goal.setDescription(goalFromNLDto.getDesc());
            goal.setTitle(goalFromNLDto.getTitle());
            goal.setQuestion(goalFromNLDto.getQuestionRemainder());

            if (goalFromNLDto.getNumDays() instanceof Integer days) {
                LocalDateTime localDateTime = start.toLocalDateTime();
                localDateTime = localDateTime.plusDays(days.longValue());
                goal.setEnd(Timestamp.valueOf(localDateTime));
            } else {
                goal.setEnd(null);
            }


            System.out.println(candidates);
        } catch (Exception e) {
            log.error("Couldn't create response");
            throw new RuntimeException("Couldn't create response");
        }


        return goal;


    }

    private Object send(String prompt) throws IOException, InterruptedException {

        String data = createData(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GENERATE_CONTENT_URL + "?key=" + apiKey))
                .POST(HttpRequest.BodyPublishers.ofString(data, StandardCharsets.UTF_8))
                .header("Content-Type", CONTENT_TYPE)
                .build();


        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the response status and handle the response body
        if (response.statusCode() == 200) {
            System.out.println("Response body: " + response.body());
            return response.body();
        } else {
            System.err.println("Error: " + response.statusCode() + " - " + response.body());
        }

        return null;


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        GeminiService s = new GeminiService();
//        String out = s.getSentimentAnalysis("Today was a good day");
        String out = s.getEmojiAnalysisMessage("23", "[Angry:50%; Sad:10%; Happy:40%]");
        System.out.println(out);
    }

}

