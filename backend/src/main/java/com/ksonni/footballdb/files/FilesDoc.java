package com.ksonni.footballdb.files;

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
 * Files Open API docs.
 */

@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Files", description = "Upload and retrieve files")
@interface FilesControllerDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Fetch file registrations with search and sort queries",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_FILES,
        parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "id",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "mimeType",
                        schema = @Schema(implementation = String.class)),
                @Parameter(in = ParameterIn.QUERY, name = "limit", description = "Max number of results per page",
                        schema = @Schema(implementation = Integer.class)),
                @Parameter(in = ParameterIn.QUERY, name = "sort",
                        description = "Comma separated list of fields to sort",
                        schema = @Schema(implementation = String.class)),
        }
)
@interface EnumerateFilesDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Retrieve a file",
        description = DocUtils.NO_PERMISSIONS
                + DocUtils.LINE_SEPARATOR + "Retrieves a file from storage. This returns the actual file rather"
                + " than just the registration."
)
@interface GetFileDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Upload a file",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_FILES
                + DocUtils.LINE_SEPARATOR + "Currently only image files under 500KB are allowed."
)
@interface UploadFileDoc {
}

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Delete a file",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_FILES
)
@interface DeleteFileDoc {
}
