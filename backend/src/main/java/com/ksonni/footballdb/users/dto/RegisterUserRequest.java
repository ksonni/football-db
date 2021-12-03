package com.ksonni.footballdb.users.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class RegisterUserRequest {

    @Email
    @Size(max = 60)
    @NotBlank
    private String emailId;

    @Size(min = 10)
    @Pattern(message = "Password must contain at least 1 number", regexp = ".*[0-9]+.*")
    @NotBlank
    private String password;

}
