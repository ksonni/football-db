package com.ksonni.footballdb.players;

import com.ksonni.footballdb.players.domain.Side;
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
 * Players Open API docs.
 */

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Players", description = "Search for and manage players")
@interface PlayersControllerDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Fetch players with search and sort queries",
        description = DocUtils.NO_PERMISSIONS + DocUtils.STANDARD_QUERY_DOC,
        parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "id",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "fullName",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "in:fullName",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "or:in:fullName",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "height", description = "Height in cm",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "lt:height",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "gt:height",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "or:gt:height",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "weight", description = "Weight in kg",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "lt:weight",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "gt:weight",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "or:gt:weight",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "preferredFoot",
                        schema = @Schema(implementation = Side.class)),
                @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "sort",
                        description = "Comma separated list of fields to sort",
                        schema = @Schema(implementation = String.class)),
        }
)
@interface EnumeratePlayersDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Register a new player",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
)
@interface RegisterPlayerDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Update an existing player",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
)
@interface PatchPlayerDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Delete an existing player",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
)
@interface DeletePlayerDoc {
}
