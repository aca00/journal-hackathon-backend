package com.hack.journal.controller;

import com.hack.journal.dto.GoalAddRequestDto;
import com.hack.journal.dto.GoalDisplayDto;
import com.hack.journal.dto.GoalUpdateRequestDto;
import com.hack.journal.entity.Goal;
import com.hack.journal.entity.GoalTracker;
import com.hack.journal.entity.User;
import com.hack.journal.service.GeminiService;
import com.hack.journal.service.GoalService;
import com.hack.journal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.concurrent.RecursiveTask;

@RestController
@CrossOrigin
@RequestMapping("api/v1/goals")
public class GoalController {
    @Autowired
    UserService userService;
    @Autowired
    private GoalService goalService;
    @Autowired
    private GeminiService geminiService;

    @GetMapping("")
    public ResponseEntity<?> fetchGoals() {
        User user = userService.getCurrentUser();
        List<Goal> goals = goalService.findAllGoals(user.getId());
        goals = goals.stream().filter(goal -> !goalService.hasDoneGoal(goal.getId()))
                .toList();


        List<GoalDisplayDto> goalDisplayDtos = new ArrayList<>();
        goals.stream().filter(Objects::nonNull).forEach(goal -> {

            int done = goal.getDone();

            double percentage;

            if (goal.getEnd() == null || goal.getStart() == null) {
                percentage = 0.0d;
            } else {
                LocalDate startLocalDate = goal.getStart().toLocalDateTime().toLocalDate();
                LocalDate endLocalDate = goal.getEnd().toLocalDateTime().toLocalDate();
                Period period = Period.between(startLocalDate, endLocalDate);
                System.out.println("period::" + period.getDays() + " done:: " + done);

                percentage = (double) done / (period.getDays() + 1);
            }


            GoalDisplayDto dto = GoalDisplayDto.builder()
                    .id(goal.getId())
                    .end(goal.getEnd())
                    .start(goal.getStart())
                    .done(goal.getDone())
                    .description(goal.getDescription())
                    .title(goal.getTitle())
                    .question(goal.getQuestion())
                    .userId(goal.getUserId())
                    .doneToday(false)
                    .progress(percentage)
                    .build();
            goalDisplayDtos.add(dto);

        });

        return ResponseEntity.ok(goalDisplayDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goal> fetchGoal(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        Goal goal = goalService.findById(id);
        if (user.getId() != goal.getUserId()) {
            throw new RuntimeException("Illegal access");
        }
        return ResponseEntity.ok(goal);
    }

    @PostMapping
    public ResponseEntity<Goal> postGoal(@RequestBody GoalAddRequestDto goalAddRequestDto) {
        User user = userService.getCurrentUser();
        Goal goal = Goal.builder().build();
        if (goalAddRequestDto.isNLPrompt()) {
            goal = geminiService.createGoalFromNLPrompt(goalAddRequestDto.getNlPrompt(), goalAddRequestDto.getStart());
        } else {
            goal.setStart(goalAddRequestDto.getStart());
            goal.setTitle(goalAddRequestDto.getTitle());
            goal.setDescription(goalAddRequestDto.getDescription());
            LocalDateTime localDateTime = goalAddRequestDto.getStart().toLocalDateTime();
            localDateTime = localDateTime.plusDays(goalAddRequestDto.getNumberOfDays());
            goal.setEnd(Timestamp.valueOf(localDateTime));
        }
        goal.setUserId(user.getId());

        goal = goalService.save(goal);
        return ResponseEntity.ok(goal);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@PathVariable("id") Long id, @RequestBody GoalUpdateRequestDto goalUpdateRequestDto) {
        User user = userService.getCurrentUser();

        Goal goal = goalService.findById(id);

        if (goalService.hasDoneGoal(id)) {
            return ResponseEntity.ok(goal);
        }

        if (goal != null) {
            if (goalUpdateRequestDto.isComplete()) {
                goal.setDone(goal.getDone() + 1);
                GoalTracker tracker = GoalTracker.builder().goalId(goal.getId())
                        .timestamp(Timestamp.valueOf(LocalDateTime.now()))
                        .build();
                goalService.saveGoalTrackingInfo(tracker);
                goalService.save(goal);
            }
        }
        return ResponseEntity.ok(goal);
    }

}
