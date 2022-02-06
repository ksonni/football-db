package com.ksonni.footballdb.files.services;

import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultFilesService implements FilesService {

    private final Tika tika = new Tika();
    private final FilesRepository filesRepository;
    private final FileStorageService storageService;

    @Value("${app.files-path}")
    private String filesPath;

    @Value("${app.file-size-limit-bytes}")
    private long fileSizeLimitBytes;

    @Override
    public FileRegistration buildFileRegistration(final String userId, final byte[] data) {
        final String id = StringUtils.uuid();
        return FileRegistration.builder().id(id).name(id)
                .mimeType(getMediaType(data).toString())
                .sizeBytes((long) data.length)
                .created(ZonedDateTime.now())
                .createdBy(userId).build();
    }

    @Override
    @Transactional
    public void saveFile(final FileRegistration registration, final byte[] data)
            throws IOException, IllegalArgumentException {
        if (registration.getSizeBytes() > fileSizeLimitBytes) {
            throw new IllegalArgumentException("File too large");
        }
        if (!registration.getMimeType().startsWith("image/")) {
            throw new IllegalArgumentException("Unsupported file format");
        }
        final var file = new File(getPath(registration.getName()));
        if (storageService.exists(file)) {
            throw new IllegalArgumentException("File already exists");
        }

        filesRepository.save(registration);
        storageService.saveFile(file, data);
    }

    @Override
    public FileRegistration findFileRegistrationById(final String id) throws FileNotRegisteredException {
        final Optional<FileRegistration> fileOptional = filesRepository.findById(id);
        if (fileOptional.isEmpty()) {
            throw new FileNotRegisteredException("File does not exist");
        }
        return fileOptional.get();
    }

    @Override
    public byte[] readFile(final FileRegistration registration) throws IOException {
        return storageService.readFile(getPath(registration.getName()));
    }

    @Override
    @Transactional
    public void deleteFile(final String id) throws IOException, FileNotRegisteredException {
        final FileRegistration registration = findFileRegistrationById(id);
        final var file = new File(getPath(registration.getName()));

        filesRepository.delete(registration);
        storageService.deleteFile(file);
    }

    @Override
    public Page<FileRegistration> queryFiles(final Query<FileRegistration> query) {
        return filesRepository.findAll(query);
    }

    private String getPath(final String name) {
        return filesPath + "/" + name;
    }

    private MediaType getMediaType(final byte[] data) {
        final String type = tika.detect(data);
        try {
            return MediaType.valueOf(type);
        } catch (InvalidMediaTypeException e) {
            return null;
        }
    }

}
