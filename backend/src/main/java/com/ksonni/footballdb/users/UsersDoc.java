package com.ksonni.footballdb.users;

import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
        @Parameter(in = ParameterIn.QUERY, name = "id"),
        @Parameter(in = ParameterIn.QUERY, name = "emailId"),
        @Parameter(in = ParameterIn.QUERY, name = "in:emailId"),
        @Parameter(in = ParameterIn.QUERY, name = "or:in:emailId"),
        @Parameter(in = ParameterIn.QUERY, name = "role"),
        @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page"),
        @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Comma separated list of fields to sort"),
    }
)
@interface EnumerateUsersDoc {}
