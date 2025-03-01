package com.ksonni.footballdb.users.dto;

import com.ksonni.footballdb.users.domain.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;

    private String emailId;

    private Role role;

}
