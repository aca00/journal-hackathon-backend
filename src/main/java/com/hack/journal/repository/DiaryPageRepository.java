package com.hack.journal.repository;

import com.hack.journal.entity.DiaryPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface DiaryPageRepository extends JpaRepository<DiaryPage, Integer> {
    @Query("from DiaryPage d order by d.createdDate DESC")
    List<DiaryPage> findByUserId(long userId);
}
