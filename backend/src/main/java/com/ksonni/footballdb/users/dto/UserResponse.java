package com.ksonni.footballdb.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;

    private String emailId;

}
