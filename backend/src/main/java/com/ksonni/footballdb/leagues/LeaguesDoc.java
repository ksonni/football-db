package com.ksonni.footballdb.leagues;

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
 * Leagues Open API docs.
 */

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Leagues", description = "Search for and manage leagues")
@interface LeaguesControllerDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Fetch leagues with search and sort queries",
        description = DocUtils.NO_PERMISSIONS + DocUtils.STANDARD_QUERY_DOC,
        parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "id",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "name",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "in:name",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "sort",
                        description = "Comma separated list of fields to sort",
                        schema = @Schema(implementation = String.class)),
        }
)
@interface EnumerateLeaguesDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Register a new league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
)
@interface RegisterLeagueDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Update an existing league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
)
@interface PatchLeagueDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Delete an existing league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
                + DocUtils.SEPARATOR + Permission.Code.MANAGE_CLUBS
                + DocUtils.SEPARATOR + Permission.Code.MANAGE_PLAYERS
                + DocUtils.LINE_SEPARATOR + "Delete a league and all its clubs and players."
)
@interface DeleteLeagueDoc {
}
