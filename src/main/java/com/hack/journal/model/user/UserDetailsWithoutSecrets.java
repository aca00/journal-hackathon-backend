package com.hack.journal.model.user;

import com.hack.journal.enums.Gender;
import com.hack.journal.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.sql.Timestamp;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserDetailsWithoutSecrets extends RepresentationModel<UserDetailsWithoutSecrets> {
    private String fullName;
    private String email;
    private Gender gender;
    private Timestamp dateOfBirth;
    private boolean enabled;
    private boolean verified;
    private Role role;
    @JsonProperty(value = "userId")
    private long id;
    private String phoneNumber;
    private String displayImageUrl;

    public UserDetailsWithoutSecrets(String fullName) {
        this.fullName = fullName;
    }
}
