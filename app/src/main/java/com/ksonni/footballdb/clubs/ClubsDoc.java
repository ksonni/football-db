package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Clubs Open API docs.
 */

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Clubs", description = "Search for and manage clubs")
@interface ClubsControllerDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Fetch clubs with search and sort queries",
        description = DocUtils.NO_PERMISSIONS + DocUtils.STANDARD_QUERY_DOC,
        parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "id",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "name",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "in:name",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "overallRating",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "lt:overallRating",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "gt:overallRating",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "gte:overallRating",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "lte:overallRating",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "limit",
                        description = "Max number of results per page",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "sort",
                        description = "Comma separated list of fields to sort",
                        schema = @Schema(implementation = String.class)),
        }
)
@interface EnumerateClubsDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Register a new club",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
)
@interface RegisterClubDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Update an existing club",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
)
@interface PatchClubDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Delete an existing club",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
                + DocUtils.SEPARATOR + Permission.Code.MANAGE_PLAYERS
                + DocUtils.LINE_SEPARATOR + "Delete a club and all its players."
)
@interface DeleteClubDoc {
}
