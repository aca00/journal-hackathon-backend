package com.hack.journal.service;

import com.hack.journal.entity.UnexpiredRevokedToken;
import com.hack.journal.repository.UnexpiredRevokedTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class TokenRevokeService {
    @Autowired
    private final UnexpiredRevokedTokenRepository unexpiredRevokedTokenRepository;

    public void revoke(UnexpiredRevokedToken token) throws Exception {
        if (token.getUser().getUnexpiredRevokedTokens().contains(token)) {
            throw new Exception("Token already revoked");
        }
        saveToken(token);
    }

    @Transactional
    private void saveToken(UnexpiredRevokedToken token) throws Exception {
        unexpiredRevokedTokenRepository.save(token);
    }

}
