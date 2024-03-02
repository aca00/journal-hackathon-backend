package com.hack.journal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "unexpired_revoked_token")
public class UnexpiredRevokedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "token_id")
    private long Id;
    @Column(name = "token_type")
    private String tokenType;
    @Column(name = "token_text")
    private String token;
    @Column(name = "issue_date")
    private Timestamp issueDate;
    @Column(name = "expiry_date")
    private Timestamp expiryDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UnexpiredRevokedToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnexpiredRevokedToken that = (UnexpiredRevokedToken) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
