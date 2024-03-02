package com.hack.journal.repository;

import com.hack.journal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(long userId);
}
