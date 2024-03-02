package com.hack.journal.repository;

import com.hack.journal.entity.*;
import com.hack.journal.model.user.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User, Long> {

    String FETCH_ALL_USERS_WITHOUT_SECRETS = "select new com.hack.journal.model.user.UserDetailsWithoutSecrets(u.fullName, u.email, u.gender, u.dateOfBirth, u.enabled, u.verified, u.role, u.id, u.phoneNumber, u.displayImageUrl) from User u";
    String FILTER_BY_FULL_NAME_AND_IS_ENABLED = FETCH_ALL_USERS_WITHOUT_SECRETS + " where upper(u.fullName) like upper(concat('%', ?1, '%')) and u.enabled = ?2";

    Optional<User> findByEmail(String email);

    @Query(FETCH_ALL_USERS_WITHOUT_SECRETS)
    List<UserDetailsWithoutSecrets> fetchUserDetailsWithoutSecrets();

    @Query(FILTER_BY_FULL_NAME_AND_IS_ENABLED)
    Page<UserDetailsWithoutSecrets> filterByFullNameAndIsEnabled(String fullName, boolean isEnabled, Pageable pageable);
}
