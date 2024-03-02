package com.hack.journal.controller;

import com.hack.journal.entity.DiaryPage;
import com.hack.journal.entity.User;
import com.hack.journal.model.response.SuccessResponse;
import com.hack.journal.service.DiaryService;
import com.hack.journal.service.GeminiService;
import com.hack.journal.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;


import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Diary Controller", description = """
        """)
@RequestMapping("api/v1/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;
    @Autowired
    private UserService userService;

    @Autowired
    private GeminiService geminiService;

    @GetMapping("diary-pages")
    ResponseEntity<List<DiaryPage>> fetchDiaryPages() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(diaryService.fetchPages(user.getId()));
    }

    @PostMapping("diary-pages")
    ResponseEntity<SuccessResponse> postDiaryPage(@NotNull @RequestBody DiaryPage diaryPage) {
        if (diaryPage == null || ((diaryPage.getTitle() == null || diaryPage.getTitle().isBlank())  &&
        (diaryPage.getContent() == null || diaryPage.getContent().isBlank()))
        ) {
            throw new RuntimeException("Empty page");
        }
        if (diaryPage.getTitle() == null || diaryPage.getTitle().isBlank()) {
            String output = geminiService.getSentimentAnalysis(diaryPage.getContent());
            if (output.contains("$")) {
                String[] outs = output.split("\\$");
                if (outs.length > 1) {
                    diaryPage.setTitle(outs[1]);
                }
                if (outs.length > 0) {
                    String emojiAuto = outs[0].trim();
                    if (List.of("Angry", "Sad", "Happy", "Neutral").contains(emojiAuto)) {
                        diaryPage.setEmojiAuto(emojiAuto);
                    }
                }
            }
        }
        if (diaryPage.getEmojiAuto() == null) {
            diaryPage.setEmojiAuto("Neutral");
        }
        diaryPage.setUserId(userService.getCurrentUser().getId());
        diaryService.save(diaryPage);
        return ResponseEntity.ok(new SuccessResponse("Saved successfully"));
    }

    @DeleteMapping("diary-pages/{id}")
    ResponseEntity<SuccessResponse> deletePage(@PathVariable("id") Integer id) {
        diaryService.delete(id, userService.getCurrentUser().getId());
        return ResponseEntity.ok(new SuccessResponse("Saved successfully"));
    }
}
