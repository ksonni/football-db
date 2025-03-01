package com.ksonni.footballdb.users.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class LoginRequest {

    @NotBlank
    private String emailId;

    @NotBlank
    private String password;

}
