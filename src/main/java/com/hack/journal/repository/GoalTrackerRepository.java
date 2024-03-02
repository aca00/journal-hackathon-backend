package com.hack.journal.repository;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.hack.journal.entity.GoalTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@RepositoryRestResource(exported = false)
public interface GoalTrackerRepository extends JpaRepository<GoalTracker, Long> {
    @Query("select g from GoalTracker g where g.goalId = :goalId and g.timestamp >= :start and g.timestamp <= :end")
    Optional<GoalTracker> hasDoneGoal(long goalId, Timestamp start, Timestamp end);
}
