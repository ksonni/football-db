package com.ksonni.footballdb.files.services;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DefaultFileStorageService implements FileStorageService {

    @Override
    public byte[] readFile(final String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    @Override
    public void saveFile(final File file, final byte[] data) throws IOException {
        FileUtils.writeByteArrayToFile(file, data);
    }

    @Override
    public void deleteFile(final File file) throws IOException {
        FileUtils.delete(file);
    }

    @Override
    public boolean exists(final File file) {
        return file.exists();
    }

}
