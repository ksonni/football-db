package com.ksonni.footballdb.players;

import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Players API docs
 */

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Players", description = "Search for and manage players")
@interface PlayersControllerDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Fetch players with search and sort queries",
    description = DocUtils.NO_PERMISSIONS + DocUtils.STANDARD_QUERY_DOC,
    parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "id"),
        @Parameter(in = ParameterIn.QUERY, name = "fullName"),
        @Parameter(in = ParameterIn.QUERY, name = "in:fullName"),
        @Parameter(in = ParameterIn.QUERY, name = "or:in:fullName"),
        @Parameter(in = ParameterIn.QUERY, name = "height", description = "Height in cm"),
        @Parameter(in = ParameterIn.QUERY, name = "lt:height"),
        @Parameter(in = ParameterIn.QUERY, name = "gt:height"),
        @Parameter(in = ParameterIn.QUERY, name = "or:gt:height"),
        @Parameter(in = ParameterIn.QUERY, name = "weight", description = "Weight in kg"),
        @Parameter(in = ParameterIn.QUERY, name = "lt:weight"),
        @Parameter(in = ParameterIn.QUERY, name = "gt:weight"),
        @Parameter(in = ParameterIn.QUERY, name = "or:gt:weight"),
        @Parameter(in = ParameterIn.QUERY, name = "preferredFoot"),
        @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page"),
        @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Comma separated list of fields to sort"),
    }
)
@interface EnumeratePlayersDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Register a new player",
    description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
)
@interface RegisterPlayerDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Update an existing player",
    description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
)
@interface PatchPlayerDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Delete an existing player",
    description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
)
@interface DeletePlayerDoc {}
