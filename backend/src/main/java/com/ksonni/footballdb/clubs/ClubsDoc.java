package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Clubs API docs
 */

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Clubs", description = "Search for and manage clubs")
@interface ClubsControllerDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Fetch clubs with search and sort queries",
    description = DocUtils.NO_PERMISSIONS + DocUtils.STANDARD_QUERY_DOC,
    parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "id"),
        @Parameter(in = ParameterIn.QUERY, name = "name"),
        @Parameter(in = ParameterIn.QUERY, name = "in:name"),
        @Parameter(in = ParameterIn.QUERY, name = "overallRating"),
        @Parameter(in = ParameterIn.QUERY, name = "lt:overallRating"),
        @Parameter(in = ParameterIn.QUERY, name = "gt:overallRating"),
        @Parameter(in = ParameterIn.QUERY, name = "gte:overallRating"),
        @Parameter(in = ParameterIn.QUERY, name = "lte:overallRating"),
        @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page"),
        @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Comma separated list of fields to sort"),
    }
)
@interface EnumerateClubsDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Register a new club",
    description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
)
@interface RegisterClubDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Update an existing club",
    description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
)
@interface PatchClubDoc {}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Delete an existing club",
    description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS +
            DocUtils.SEPARATOR + Permission.Code.MANAGE_PLAYERS  +
            "\n\nDelete a club and all its players."
)
@interface DeleteClubDoc {}
