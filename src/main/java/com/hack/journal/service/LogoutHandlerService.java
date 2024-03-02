package com.hack.journal.service;

import com.hack.journal.entity.UnexpiredRevokedToken;
import com.hack.journal.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final com.hack.journal.service.TokenRevokeService tokenRevokeService;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userName = jwtService.extractUserName(jwt);
        final User currentUser = userService.findUserByEmail(userName).orElseThrow();


        System.out.println("\n\nAuthentication");
        System.out.println(authentication);

        UnexpiredRevokedToken unexpiredRevokedToken = UnexpiredRevokedToken.builder()
                .token(jwt)
                .tokenType("BEARER")
                .user(currentUser)
                .issueDate(new Timestamp(jwtService.extractIssueDate(jwt).getTime()))
                .expiryDate(new Timestamp(jwtService.extractExpiryDate(jwt).getTime()))
                .build();

        try {
            tokenRevokeService.revoke(unexpiredRevokedToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
