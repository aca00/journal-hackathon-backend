package com.hack.journal.service;

import com.hack.journal.entity.Goal;
import com.hack.journal.entity.GoalTracker;
import com.hack.journal.repository.GoalRepository;
import com.hack.journal.repository.GoalTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GoalService {
    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalTrackerRepository goalTrackerRepository;

    public Goal save(Goal goal) {
        return goalRepository.save(goal);
    }

    public void delete(Goal goal) {
        goalRepository.delete(goal);
    }

    public Goal findById(long id) {
        return goalRepository.findById(id).orElse(null);
    }

//    public List<Goal> findAllGoals(long userId) {
//        return goalRepository.findByUserId(userId);
//    }

    public List<Goal> findAllGoals(long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return goals.stream()
                .filter(Objects::nonNull)
                .filter(x -> {
                    if (x.getEnd() == null) return true; // end date not specified
                    return x.getEnd().after(currentTime);
                }).toList();
    }

    public boolean hasDoneGoal(long goalId) {
        LocalDate currentDate = LocalDate.now();
        Timestamp start = Timestamp.valueOf(currentDate.atStartOfDay());
        Timestamp end = Timestamp.valueOf(currentDate.atTime(LocalTime.MAX));
        Optional<GoalTracker> opt = goalTrackerRepository.hasDoneGoal(goalId, start, end);
        return opt.isPresent();
    }

    public void saveGoalTrackingInfo(GoalTracker goalTracker) {
        goalTrackerRepository.save(goalTracker);
    }


}
