package com.hack.journal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "user_verification_code")
public class UserVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "_id")
    private int Id;
    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "expiry_date")
    private Timestamp expiryDate;
    @Column(name = "user_id")
    private Long userId;
}
