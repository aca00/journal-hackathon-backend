package com.hack.journal.service;

import com.hack.journal.entity.TemporaryCredentialHolder;
import com.hack.journal.entity.User;
import com.hack.journal.entity.UserVerificationCode;
import com.hack.journal.enums.Role;
import com.hack.journal.exception.UserAlreadyCreatedException;
import com.hack.journal.model.response.SuccessResponse;
import com.hack.journal.model.user.AuthenticationRequest;
import com.hack.journal.model.user.AuthenticationResponse;
import com.hack.journal.model.user.RegisterRequest;
import com.hack.journal.model.user.ResetPasswordRequest;
import com.hack.journal.repository.TemporaryCredentialHolderRepository;
import com.hack.journal.repository.UserRepository;
import com.hack.journal.repository.UserVerificationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final TemporaryCredentialHolderRepository temporaryCredentialHolderRepository;
    @Autowired
    private final UserService userService;

    @Autowired
    private final UserVerificationRepository userVerificationRepository;

    @Autowired
    private final EmailSenderService emailSenderService;

    private static final String BASE_URL = "http://127.0.0.1:9090/api/v1/users/verify?token=";

    public AuthenticationResponse register(RegisterRequest request) throws Exception {


        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new UserAlreadyCreatedException("User with this email already exists");
        });

        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .verified(false)
                .role(Role.ROLE_USER)
                .build();

        try {
            userRepository.save(user);
            logger.info("User saved successfully");
        } catch (Exception e) {
            throw new Exception("Couldn't register");
        }

        var jwtToken = jwtService.generateToken(user);

        UserVerificationCode code = userVerificationRepository.findByUserId(user.getId())
                .orElse(UserVerificationCode.builder()
                        .build());

        code.setVerificationCode(jwtToken);
        code.setUserId(user.getId());

        userVerificationRepository.save(code);

        String emailBody = "Dear " + user.getFullName() + ",\n" + """
                Click this link to verify your email address:
                """ + "\n\n" + BASE_URL + jwtToken;

        emailSenderService.sendMail(user.getEmail(), "Verify your email", emailBody);


        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = null;

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find User with email " + request.getEmail()));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    @Transactional
    public SuccessResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow();
        TemporaryCredentialHolder temporaryCredentialHolder = temporaryCredentialHolderRepository.findById(user.getId()).orElseThrow();
        user.setPassword(temporaryCredentialHolder.getPassword());
        userRepository.save(user);
        temporaryCredentialHolderRepository.delete(temporaryCredentialHolder);
        return new SuccessResponse("Password changed");
    }

    @Transactional
    public SuccessResponse requestPasswordReset(ResetPasswordRequest resetPasswordRequest) throws Exception {
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow();
        if (resetPasswordRequest.getPassword() == null) {
            throw new Exception("Password field can't be empty");
        }
        TemporaryCredentialHolder temporaryCredentialHolder = temporaryCredentialHolderRepository.findById(user.getId()).orElse(
                TemporaryCredentialHolder.builder()
                        .userId(user.getId())
                        .build()
        );
        temporaryCredentialHolder.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        temporaryCredentialHolderRepository.save(temporaryCredentialHolder);

        return (SuccessResponse) userService.requestVerification(user).getBody();
    }
}
