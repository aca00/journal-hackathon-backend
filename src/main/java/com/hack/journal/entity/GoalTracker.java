package com.hack.journal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "goal_tracker")
public class GoalTracker {
    @Id
    @GeneratedValue
    @Column(name = "_id")
    private Long id;
    @Column(name = "goal_id")
    private long goalId;
    private Timestamp timestamp;
}
