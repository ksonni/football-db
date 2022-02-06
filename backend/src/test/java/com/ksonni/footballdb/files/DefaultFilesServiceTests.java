package com.ksonni.footballdb.files;

import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.files.services.DefaultFilesService;
import com.ksonni.footballdb.files.services.FileStorageService;
import com.ksonni.footballdb.files.services.FilesRepository;
import com.ksonni.footballdb.files.services.FilesService;
import com.ksonni.footballdb.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class DefaultFilesServiceTests {

    private static final long MAX_FILE_SIZE = 1000;
    private static final String FILE_ID = "id";
    private static final String USER_ID = "uid";
    private static final String IMAGE_MIME_TYPE = "image/png";

    @MockBean
    private FilesRepository filesRepository;
    @MockBean
    private FileStorageService storageService;

    private FilesService filesService;

    private FileRegistration.FileRegistrationBuilder validRegistration() {
        return FileRegistration.builder()
                .id(FILE_ID).mimeType(IMAGE_MIME_TYPE).sizeBytes(MAX_FILE_SIZE)
                .created(ZonedDateTime.now()).createdBy(USER_ID);
    }

    @BeforeEach
    void setup() {
        filesService = new DefaultFilesService(filesRepository, storageService);
        ReflectionTestUtils.setField(filesService, "filesPath", "/");
        ReflectionTestUtils.setField(filesService, "fileSizeLimitBytes", MAX_FILE_SIZE);
    }

    @Test
    void saveFileEnforcesLimits() throws IOException {
        final FileRegistration reg = validRegistration().sizeBytes(MAX_FILE_SIZE + 1).build();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            filesService.saveFile(reg, new byte[] {});
        });
    }

    @Test
    void saveFileValidatesMimeType() throws IOException {
        final FileRegistration reg = validRegistration().mimeType("invalid").build();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            filesService.saveFile(reg, new byte[] {});
        });
    }

    @Test
    void saveFileDoesntOverwriteAnExistingFile() throws IOException {
        final FileRegistration reg = validRegistration().build();
        BDDMockito.given(storageService.exists(ArgumentMatchers.any())).willReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            filesService.saveFile(reg, new byte[] {});
        });
    }

    @Test
    void saveFileTest() throws IOException {
        final FileRegistration reg = validRegistration().build();
        final var data = new byte[] {};
        filesService.saveFile(reg, data);
        BDDMockito.verify(filesRepository, Mockito.times(1)).save(reg);
        BDDMockito.verify(storageService, Mockito.times(1))
                .saveFile(ArgumentMatchers.any(), ArgumentMatchers.eq(data));
    }

    @Test
    void buildFileRegistration() throws IOException {
        final byte[] imageFile = FileUtils.loadResource("image.png");
        final FileRegistration registration = filesService.buildFileRegistration(USER_ID, imageFile);
        Assertions.assertNotNull(registration.getId());
        Assertions.assertNotNull(registration.getName());
        Assertions.assertEquals(IMAGE_MIME_TYPE, registration.getMimeType());
        Assertions.assertEquals(imageFile.length, registration.getSizeBytes());
        Assertions.assertEquals(USER_ID, registration.getCreatedBy());
    }

    @Test
    void findRegistrationByIdNotFound() {
        BDDMockito.given(filesRepository.findById(FILE_ID)).willReturn(Optional.empty());
        Assertions.assertThrows(FilesService.FileNotRegisteredException.class, () -> {
            filesService.findFileRegistrationById(FILE_ID);
        });
    }

    @Test
    void deleteFile() throws Exception {
        final var registration = FileRegistration.builder().id(FILE_ID).build();
        BDDMockito.given(filesRepository.findById(FILE_ID))
                .willReturn(Optional.ofNullable(registration));

        filesService.deleteFile(FILE_ID);

        BDDMockito.verify(filesRepository, Mockito.times(1))
                .delete(registration);
        BDDMockito.verify(storageService, Mockito.times(1))
                .deleteFile(ArgumentMatchers.any());
    }

    @Test
    void deleteFileNotFound() {
        BDDMockito.given(filesRepository.findById(FILE_ID)).willReturn(Optional.empty());
        Assertions.assertThrows(FilesService.FileNotRegisteredException.class, () -> {
            filesService.deleteFile(FILE_ID);
        });
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(filesRepository, storageService);
    }

}
