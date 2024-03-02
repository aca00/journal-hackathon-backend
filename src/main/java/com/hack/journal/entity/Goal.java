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
@Table(name = "_goal", schema = "public")
public class Goal {
    @Id
    @GeneratedValue
    @Column(name = "_id")
    private Long id;
    private String title;
    private String description;
    @Column(name = "_start")
    private Timestamp start;
    @Column(name = "_end")
    private Timestamp end;
    private Boolean active;
    private String question;
    private int done;
    @Column(name = "user_id")
    private Long userId;
}
