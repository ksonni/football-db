package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Leagues API docs
 */

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Leagues", description = "Search for and manage leagues")
@interface LeaguesControllerDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Fetch leagues with search and sort queries",
        description = DocUtils.NO_PERMISSIONS + DocUtils.STANDARD_QUERY_DOC,
        parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "id"),
            @Parameter(in = ParameterIn.QUERY, name = "name"),
            @Parameter(in = ParameterIn.QUERY, name = "in:name"),
            @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page"),
            @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Comma separated list of fields to sort"),
        }
)
@interface EnumerateLeaguesDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Register a new league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
)
@interface RegisterLeagueDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Update an existing league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
)
@interface PatchLeagueDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Delete an existing league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES +
                DocUtils.SEPARATOR + Permission.Code.MANAGE_CLUBS  +
                DocUtils.SEPARATOR + Permission.Code.MANAGE_PLAYERS  +
                "\n\nDelete a league and all its clubs and players."
)
@interface DeleteLeagueDoc {}
