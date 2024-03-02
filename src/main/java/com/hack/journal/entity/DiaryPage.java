package com.hack.journal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryPage {
    @Column(name = "_ic")
    @Id
    @GeneratedValue
    private Integer id;
    private Long userId;
    private String title;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String content;
    private String emoji;
    private Boolean isProtected;
    private String emojiAuto;
}
