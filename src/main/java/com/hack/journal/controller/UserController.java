package com.hack.journal.controller;

import com.hack.journal.entity.User;
import com.hack.journal.exception.UserVerificationException;
import com.hack.journal.model.response.ErrorResponse;
import com.hack.journal.model.response.SuccessResponse;
import com.hack.journal.model.user.*;
import com.hack.journal.service.AuthenticationService;
import com.hack.journal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "User Controller", description = """
        Manages all activities of user\n
        Some functionalities are limited to admin.
        """)
@RestController
@CrossOrigin
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private final AuthenticationService authenticationService;
    @Autowired
    private final UserService userService;

    @Autowired
    private PagedResourcesAssembler<UserDetailsWithoutSecrets> pagedResourcesAssembler;


    @PostMapping("/signup")
    @Operation(summary = "Roles: Any")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) throws Exception {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @CrossOrigin
    @PostMapping("/signin")
    @Operation(summary = "Roles: Any  Sign in with email and password")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully signed in",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = AuthenticationResponse.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));

    }

    @GetMapping("/verify")
    @Operation(summary = "Roles: Any")
    public ResponseEntity<?> requestVerification(@RequestParam String token) throws Exception {
        log.debug("Token : " + token);
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Token invalid");
        }

        return userService.verifyUser(token);
    }


//    @PostMapping("reset-password")
//    @CrossOrigin
//    @Operation(summary = "Roles: ANY")
//    public ResponseEntity<?> resetPassword(
//            @RequestBody ResetPasswordRequest resetPasswordRequest
//    ) throws Exception {
//        SuccessResponse response = new SuccessResponse("error");
//        if (resetPasswordRequest.getCode() != null) {
//            User user = userService.findUserByEmail(resetPasswordRequest.getEmail()).orElseThrow();
//            ResponseEntity<?> userVerifiedResponse = userService.verifyUser(resetPasswordRequest.getCode(), user);
//            if (userVerifiedResponse.getStatusCode() == HttpStatus.OK) {
//                response = authenticationService.resetPassword(resetPasswordRequest);
//            } else {
//                throw new UserVerificationException("Code mismatch");
//            }
//        } else {
//            response = authenticationService.requestPasswordReset(resetPasswordRequest);
//        }
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    @GetMapping("/self")
    @Operation(summary = "Roles: USER")
    public ResponseEntity<UserDetailsWithoutSecrets> getUserDetails() {
        User currentUser = userService.getCurrentUser();
        UserDetailsWithoutSecrets userDetails = UserDetailsWithoutSecrets.builder()
                .fullName(currentUser.getFullName())
                .id(currentUser.getId())
                .dateOfBirth(currentUser.getDateOfBirth())
                .email(currentUser.getEmail())
                .gender(currentUser.getGender())
                .role(currentUser.getRole())
                .enabled(currentUser.isEnabled())
                .verified(currentUser.isVerified())
                .build();

        userDetails.add(
                linkTo(methodOn(UserController.class).getUserDetails())
                        .withSelfRel()
        );
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @PatchMapping("/self")
    @Operation(summary = "Roles: USER")
    public ResponseEntity<UserDetailsWithoutSecrets> updateUserProfileSelf(
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        long userId = userService.getCurrentUser().getId();
        return updateUser(userId, userUpdateRequest);
    }

    @Deprecated
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedModel<EntityModel<UserDetailsWithoutSecrets>> filterUsers(
            @RequestParam(defaultValue = "%") String fullName,
            @RequestParam(defaultValue = "true") boolean isEnabled,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") List<String> sortList,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortOrder
    ) {

        Page<UserDetailsWithoutSecrets> filteredUsersPage = userService.filterByFullNameAndIsEnabled(
                fullName,
                isEnabled,
                pageNumber,
                size,
                sortList,
                sortOrder.toString());

//        return new ResponseEntity<>(filteredUsers, HttpStatus.OK);
        return pagedResourcesAssembler.toModel(filteredUsersPage);
    }


    @GetMapping("/{userId}")
    @Operation(summary = "Roles: ADMIN")
    public ResponseEntity<UserDetailsWithoutSecrets> getUserDetailsById(@PathVariable long userId) {
        User user = userService.findUserById(userId).orElseThrow();
        UserDetailsWithoutSecrets userDetails = createUserDetailsWithoutSecrets(user);
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Roles: ADMIN")
    public ResponseEntity<ErrorResponse> deleteUserById(
            @PathVariable long userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>(new ErrorResponse("Deleted user " + userId, "Success"), HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Roles: ADMIN")
    public ResponseEntity<UserDetailsWithoutSecrets> updateUser(@PathVariable long userId,
                                                                @RequestBody UserUpdateRequest userUpdateRequest) {
        User user = userService.updateUserDetails(userId, userUpdateRequest);
        return new ResponseEntity<>(createUserDetailsWithoutSecrets(user), HttpStatus.OK);
    }




    private UserDetailsWithoutSecrets createUserDetailsWithoutSecrets(User user) {
        UserDetailsWithoutSecrets userDetails = UserDetailsWithoutSecrets.builder()
                .fullName(user.getFullName())
                .id(user.getId())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .gender(user.getGender())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .verified(user.isVerified())
                .build();

        userDetails.add(
                linkTo(methodOn(UserController.class).getUserDetails())
                        .withSelfRel());

        return userDetails;
    }
}
