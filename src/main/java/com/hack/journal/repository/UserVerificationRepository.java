package com.hack.journal.repository;

import com.hack.journal.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
@RepositoryRestResource(exported = false)
public interface UserVerificationRepository extends JpaRepository<UserVerificationCode, Integer> {
    Optional<UserVerificationCode> findByUserId(long userId);
//    Optional<UserVerificationCode> findByUser(User user);
    void deleteByUserId(long id);
}
