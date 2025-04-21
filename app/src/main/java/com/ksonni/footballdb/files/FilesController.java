package com.ksonni.footballdb.files;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.files.dto.FileRegistrationResponse;
import com.ksonni.footballdb.files.services.FilesMapper;
import com.ksonni.footballdb.files.services.FilesService;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = RoutesConfig.Files.PATH)
@RequiredArgsConstructor
@Tag(name = "Files", description = "Upload and retrieve files")
public class FilesController {

    private final FilesService filesService;
    private final AuthService authService;
    private final FilesMapper filesMapper;

    /**
     * Retrieves a file.
     *
     * @param id id of the files file entry
     * @return contents of the file as bytes
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Retrieve a file",
        description = DocUtils.NO_PERMISSIONS
            + DocUtils.LINE_SEPARATOR + "Retrieves a file from storage. This returns the actual file rather"
            + " than just the registration."
    )
    public ResponseEntity<byte[]> getFile(final @PathVariable("id") String id) {
        final FileRegistration registration;
        try {
            registration = filesService.findFileRegistrationById(id);
        } catch (FilesService.FileNotRegisteredException e) {
            log.info("Attempting to read unregistered file: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        final byte[] data;
        try {
            data = filesService.readFile(registration);
        } catch (IOException e) {
            log.error("Failed to read file from disk: {} {}", registration.getName(), e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read file");
        }

        log.info("Reading file: {}", registration.getId());
        return ResponseEntity.ok().contentType(registration.getMediaType()).body(data);
    }

    /**
     * Accepts a multipart file upload of any supported files.
     *
     * @param file file to persist
     * @return registration of the upload file
     */
    @PostMapping
    @RolesAllowed({Permission.Code.MANAGE_FILES})
    @Operation(
        summary = "Upload a file",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_FILES
            + DocUtils.LINE_SEPARATOR + "Currently only image files under 500KB are allowed."
    )
    public FileRegistrationResponse uploadFile(final @PathVariable("file") MultipartFile file) {
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must include a valid file");
        }
        final byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            log.info("Attempting to perform empty file upload");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to process file upload");
        }

        final User user = authService.getAuthenticatedUser();
        final FileRegistration registration = filesService.buildFileRegistration(user.getId(), data);
        try {
            filesService.saveFile(registration, data);
        } catch (IllegalArgumentException e) {
            log.info("Received a bad file: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            log.error("Failed to write file: {} {}", registration.getName(), e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to write file");
        }

        log.info("Saved file: {}", registration.getId());
        return filesMapper.toFileRegistrationResponse(registration);
    }

    /**
     * Deletes a file.
     *
     * @param id id of the file
     */
    @DeleteMapping("/{id}")
    @RolesAllowed({Permission.Code.MANAGE_FILES})
    @Operation(
        summary = "Delete a file",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_FILES
    )
    public void deleteFile(final @PathVariable("id") String id) {
        try {
            filesService.deleteFile(id);
        } catch (FilesService.FileNotRegisteredException e) {
            log.info("Attempting to delete unknown file: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IOException e) {
            log.error("Failed to delete file: {} {}", id, e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file");
        }
        log.info("Deleted file: {}", id);
    }

}
