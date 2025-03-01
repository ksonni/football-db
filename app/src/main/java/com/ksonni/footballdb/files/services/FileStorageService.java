package com.ksonni.footballdb.files.services;

import java.io.File;
import java.io.IOException;

public interface FileStorageService {

    /**
     * Reads a file as a byte array.
     *
     * @param path path the file is stored in
     * @return File as a byte array
     */
    byte[] readFile(String path) throws IOException;

    /**
     * Writes a file to disk.
     *
     * @param file reference of the file to write data to
     * @param data data to be written as a byte array
     * @throws IOException if the operation fails
     */
    void saveFile(File file, byte[] data) throws IOException;

    /**
     * Delete a file from disk.
     *
     * @param file reference of the file to delete
     * @throws IOException if the operation fails
     */
    void deleteFile(File file) throws IOException;

    /**
     * Determines whether a file exists.
     *
     * @param file a file
     * @return true if it exists in storage.
     */
    boolean exists(File file);

}
