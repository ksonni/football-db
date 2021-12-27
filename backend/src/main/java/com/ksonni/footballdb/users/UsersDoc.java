package com.ksonni.footballdb.users;

import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Auth", description = "User registration and auth")
@interface AuthControllerDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Register a new user account")
@interface RegisterUserDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Login to start a new session")
@interface LoginDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Logout")
@interface LogoutDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Fetch details about the logged in user")
@interface MeDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Users", description = "Search for and manage users")
@interface UsersControllerDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Fetch users with search and sort queries",
    description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_USERS +
            "\n\n" + DocUtils.STANDARD_QUERY_DOC,
    parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "id",
            schema = @Schema(implementation = String.class)),
        @Parameter(in = ParameterIn.QUERY, name = "emailId",
            schema = @Schema(implementation = String.class)),
        @Parameter(in = ParameterIn.QUERY, name = "in:emailId",
            schema = @Schema(implementation = String.class)),
        @Parameter(in = ParameterIn.QUERY, name = "or:in:emailId",
            schema = @Schema(implementation = String.class)),
        @Parameter(in = ParameterIn.QUERY, name = "role",
            schema = @Schema(implementation = Role.class)),
        @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page",
            schema = @Schema(implementation = Integer.class)),
        @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Comma separated list of fields to sort",
            schema = @Schema(implementation = String.class)),
    }
)
@interface EnumerateUsersDoc {}
