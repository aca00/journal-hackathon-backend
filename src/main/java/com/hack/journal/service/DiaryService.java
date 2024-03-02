package com.hack.journal.service;

import com.hack.journal.entity.DiaryPage;
import com.hack.journal.repository.DiaryPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {
    @Autowired
    private DiaryPageRepository diaryPageRepository;

    public List<DiaryPage> fetchPages(long userId) {
        return diaryPageRepository.findByUserId(userId);
    }

    public void save(DiaryPage diaryPage) {
        diaryPageRepository.save(diaryPage);
    }

    public void delete(int diaryPageId, long userId) {
        DiaryPage diaryPage = diaryPageRepository.findById(diaryPageId).orElseThrow(
                () -> new RuntimeException("Couldn't find the page"));
        if (diaryPage.getUserId() != userId) {
            throw new RuntimeException("Unauthorized ");
        }
        diaryPageRepository.delete(diaryPage);
    }

}
