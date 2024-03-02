package com.hack.journal.repository;

import com.hack.journal.entity.UnexpiredRevokedToken;
import com.hack.journal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
@RepositoryRestResource(exported = false)
public interface UnexpiredRevokedTokenRepository extends JpaRepository<UnexpiredRevokedToken, Long> {
    Optional<UnexpiredRevokedToken> findByUser(User user);

}
