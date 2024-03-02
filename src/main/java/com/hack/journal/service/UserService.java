package com.hack.journal.service;

import com.hack.journal.entity.*;
import com.hack.journal.enums.Gender;
import com.hack.journal.enums.Role;
import com.hack.journal.exception.UserVerificationException;
import com.hack.journal.model.response.SuccessResponse;
import com.hack.journal.model.user.UserDetailsWithoutSecrets;
import com.hack.journal.model.user.UserUpdateRequest;
import com.hack.journal.repository.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@Data
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Autowired
    JwtService jwtService;


    @Autowired
    final com.hack.journal.service.EmailSenderService emailSenderService;
    @Value("${config.purchase-limit.role-user}")
    private double PURCHASE_LIMIT_FOR_ROLE_USER;
    @Value("${config.purchase-limit.role-agent}")
    private double PURCHASE_LIMIT_FOR_ROLE_AGENT;
    @Value("${config.order-return-period.days}")
    private long orderReturnPeriod;


    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updateUserDetails(long userId, UserUpdateRequest userUpdateRequest) {
        User user = findUserById(userId).orElseThrow();
        String fullName = userUpdateRequest.getFullName();
        String email = userUpdateRequest.getEmail();
        Gender gender = userUpdateRequest.getGender();
        Timestamp dateOfBirth = userUpdateRequest.getDateOfBirth();
        String phoneNumber = userUpdateRequest.getPhoneNumber();
        String displayImageUrl = userUpdateRequest.getDisplayImageUrl();
        Role role = userUpdateRequest.getRole();

        if (fullName != null) {
            user.setFullName(fullName);
        }
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            user.setVerified(false);
        }
        if (gender != null) {
            user.setGender(gender);
        }
        if (dateOfBirth != null) {
            user.setDateOfBirth(dateOfBirth);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }
        if (role != null && getCurrentUser().getRole() == Role.ROLE_ADMIN) {
            user.setRole(role);
        }

        if (displayImageUrl != null) {
            user.setDisplayImageUrl(displayImageUrl);
        }

        return saveUser(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public ResponseEntity<?> requestVerification() throws Exception {
        User user = getCurrentUser();
        return requestVerification(user);
    }

    /**
     * Request OTP verification
     *
     * @return {@link ResponseEntity}
     */

    @Transactional
    public ResponseEntity<?> requestVerification(User user) throws Exception {

        UserVerificationCode userVerificationCode;

        Optional<UserVerificationCode> verificationCode = userVerificationRepository.findByUserId(user.getId());

        userVerificationCode = verificationCode.orElseGet(() -> UserVerificationCode.builder()
                .userId(user.getId()).build()
        );

        int randomCode = new Random().nextInt(1000, 9000);

        userVerificationCode.setVerificationCode(String.valueOf(randomCode));
        userVerificationCode.setExpiryDate(new Timestamp(System.currentTimeMillis() + 1000 * 60 * 50));
        userVerificationRepository.save(userVerificationCode);

        emailSenderService.sendMail(user.getEmail(), "Verification code", "VerificationCode : " + randomCode);

        return new ResponseEntity<>(new SuccessResponse("VerificationCode : " + randomCode), HttpStatus.OK);
    }

    public ResponseEntity<?> verifyUser(String token) throws Exception {

        String userName = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(userName).orElseThrow(
                () -> new UsernameNotFoundException("Couldn't find user"));

        UserVerificationCode userVerificationCode = userVerificationRepository
                .findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("User hasn't requested for verification"));
        if (!jwtService.isTokenValid(token, user) || userVerificationCode.getVerificationCode().isBlank() ||
                !userVerificationCode.getVerificationCode().equals(token)) {
            throw new RuntimeException("Invalid token");
        }
        user.setVerified(true);
        userRepository.save(user);
        return new ResponseEntity<>(new SuccessResponse("User verified."), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> verifyUser(String code, User user) throws Exception {


        Optional<UserVerificationCode> verificationCode = userVerificationRepository.findByUserId(user.getId());

        if (verificationCode.isEmpty()) {
            throw new UserVerificationException("User hasn't requested for verification code");
        }

        String generatedCode = verificationCode.get().getVerificationCode();
        Timestamp expiry = verificationCode.get().getExpiryDate();

        if (!generatedCode.equals(code)) {
            throw new UserVerificationException(("Code mismatch"));
        }

        if (expiry.before(new Timestamp(System.currentTimeMillis()))) {
            throw new UserVerificationException("Code expired. Create new one");
        }

        user.setVerified(true);
        userRepository.save(user);

        userVerificationRepository.delete(verificationCode.get());

        return new ResponseEntity<>(new SuccessResponse("User verified."), HttpStatus.OK);
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<UserDetailsWithoutSecrets> getAllUsersWithoutSecrets() {
        return userRepository.fetchUserDetailsWithoutSecrets();
    }

    public Page<UserDetailsWithoutSecrets> filterByFullNameAndIsEnabled(String fullName, boolean isEnabled,
                                                                        int pageNumber, int size,
                                                                        List<String> sortList, String sortOrder) {


        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(createSortOrder(sortList, sortOrder)));
        return userRepository.filterByFullNameAndIsEnabled(fullName, isEnabled, pageable);
    }

    public List<Sort.Order> createSortOrder(List<String> sortList,
                                            String sortDirection) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort.Direction direction;

        for (String sort : sortList) {
            if (sortDirection != null) {
                direction = Sort.Direction.fromString(sortDirection);
            } else {
                direction = Sort.Direction.ASC;
            }
            sorts.add(new Sort.Order(direction, sort));
        }
        return sorts;
    }

}
