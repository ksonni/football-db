package com.ksonni.footballdb.users.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Builder
public class RegisterUserRequest {

    private static final int MAX_EMAIL_LEN = 60;
    private static final int MIN_PASSWORD_LEN = 10;

    @Email
    @Size(max = MAX_EMAIL_LEN)
    @NotBlank
    private String emailId;

    @Size(min = MIN_PASSWORD_LEN)
    @Pattern(message = "Password must contain at least 1 number", regexp = ".*[0-9]+.*")
    @NotBlank
    private String password;

}
