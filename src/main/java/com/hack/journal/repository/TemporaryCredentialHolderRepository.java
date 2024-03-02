package com.hack.journal.repository;

import com.hack.journal.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface TemporaryCredentialHolderRepository extends JpaRepository<TemporaryCredentialHolder, Long> {

}
