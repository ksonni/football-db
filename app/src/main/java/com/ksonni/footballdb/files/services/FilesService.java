package com.ksonni.footballdb.files.services;

import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.queryparser.Query;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface FilesService {

    /**
     * Parses metadata of the file and creates a registration object.
     *
     * @param userId user that is creating the file
     * @param data   contents of the file
     * @return file registration
     */
    FileRegistration buildFileRegistration(String userId, byte[] data);

    /**
     * Writes a file to disk and registers it.
     *
     * @param fileRegistration registration data for the file
     * @param data             contents of the file as bytes
     */
    void saveFile(FileRegistration fileRegistration, byte[] data)
            throws IOException, IllegalArgumentException;

    /**
     * Finds a file registration by its id.
     *
     * @param id id of the registered file
     * @return the registration
     */
    FileRegistration findFileRegistrationById(String id) throws FileNotRegisteredException;

    /**
     * Retrieves a file from disk.
     *
     * @param registration the file registration
     * @return contents of the file
     * @throws IOException when reading the file fails
     */
    byte[] readFile(FileRegistration registration) throws IOException;

    /**
     * Delete a file from disk and the associated registration.
     *
     * @param id id of the file to be deleted
     * @throws IOException when deleting the file fails
     */
    void deleteFile(String id) throws IOException, FileNotRegisteredException;

    /**
     * Queries to find a paginated list of file registrations.
     *
     * @param query query to execute
     * @return paginated list of registrations
     */
    Page<FileRegistration> queryFiles(Query<FileRegistration> query);

    class FileNotRegisteredException extends Exception {
        public FileNotRegisteredException(final String message) {
            super(message);
        }
    }

}
