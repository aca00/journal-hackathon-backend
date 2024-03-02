package com.hack.journal.model.user;

import com.hack.journal.enums.Gender;
import com.hack.journal.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    private String fullName;
    @Email(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String email;
    private Gender gender;
    private Timestamp dateOfBirth;
    private String displayImageUrl;
    private String phoneNumber;
    private Role role;
}
