package com.hack.journal.model.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {
    private String code;
    private String email;
    private String password; 
}
