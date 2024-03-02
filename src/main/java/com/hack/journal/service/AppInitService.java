package com.hack.journal.service;

import com.hack.journal.entity.User;
import com.hack.journal.enums.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppInitService {
    @Autowired
    private final UserService userService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Value("${config.root-user-name}")
    private String rootUserName;
    @Value("${config.root-user-email}")
    private String rootUserEmail;
    @Value("${config.root-user-password}")
    private String rootUserPassword;
    Logger logger = LoggerFactory.getLogger(AppInitService.class);

    @Transactional
    public void configRootUser() {
        User user = userService.findUserByEmail(rootUserEmail).orElse(
                User.builder()
                        .verified(true)
                        .enabled(true)
                        .email(rootUserEmail)
                        .role(Role.ROLE_ADMIN)
                        .password(passwordEncoder.encode(rootUserPassword))
                        .build()
        );

        userService.saveUser(user);

        logger.info("Created Root user successfully");
    }


}
