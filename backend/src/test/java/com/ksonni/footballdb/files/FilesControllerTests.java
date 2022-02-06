package com.ksonni.footballdb.files;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.files.dto.FileRegistrationResponse;
import com.ksonni.footballdb.files.services.FilesMapper;
import com.ksonni.footballdb.files.services.FilesService;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.utils.MockMvcUtils;
import com.ksonni.footballdb.utils.MockUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


@WebMvcTest(FilesController.class)
public class FilesControllerTests {

    private static final String USER_ID = "user1";
    private static final long RANDOM_FILE_SIZE = 10;
    private static final String FILE_ID = "id1";
    private static final String FILE_PATH = RoutesConfig.Files.PATH + "/" + FILE_ID;
    private static final byte[] FILE_CONTENTS = new byte[] {0, 0, 1, 1, 0};
    private static final String FILE_PARAM = "file";
    private final MockMvcUtils utils = new MockMvcUtils();

    @MockBean
    private FilesService filesService;
    @MockBean
    private AuthService authService;
    @MockBean
    private QueryParser<FileRegistration> queryParser;
    @MockBean
    private FilesMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private RateLimitingService rateLimitingService;

    private List<FileRegistration> files;

    @BeforeEach
    void setup() {
        files = Arrays.asList(
                FileRegistration.builder().id("id1").name("file1").created(ZonedDateTime.now())
                        .createdBy(USER_ID).mimeType("image/png").sizeBytes(RANDOM_FILE_SIZE).build(),
                FileRegistration.builder().id("id2").name("file2").created(ZonedDateTime.now())
                        .createdBy(USER_ID).mimeType("image/png").sizeBytes(RANDOM_FILE_SIZE).build()
        );

        final Page<FileRegistration> pagedFiles = new PageImpl<>(files,
                PageRequest.of(0, files.size()), files.size());
        BDDMockito.given(filesService.queryFiles(ArgumentMatchers.any()))
                .willReturn(pagedFiles);

        for (FileRegistration file : files) {
            BDDMockito.given(mapper.toFileRegistrationResponse(file)).willReturn(
                    FileRegistrationResponse.builder().id(file.getId())
                            .name(file.getName()).created(file.getCreated())
                            .createdBy(file.getCreatedBy()).sizeBytes(file.getSizeBytes())
                            .mimeType(file.getMimeType()).build()
            );
        }

        MockUtils.disableRateLimiting(rateLimitingService);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_FILES})
    void enumerateFiles() throws Exception {
        var expectation = mockMvc.perform(utils.get(RoutesConfig.Files.PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content",
                        Matchers.hasSize(files.size())));

        for (int i = 0; i < files.size(); i++) {
            final String content = "$.content[" + i + "]";
            final FileRegistration file = files.get(i);
            expectation = expectation
                    .andExpect(MockMvcResultMatchers.jsonPath(content + ".id", Is.is(file.getId())))
                    .andExpect(MockMvcResultMatchers.jsonPath(content + ".name", Is.is(file.getName())))
                    .andExpect(MockMvcResultMatchers.jsonPath(content + ".mimeType",
                            Is.is(file.getMimeType())))
                    .andExpect(MockMvcResultMatchers.jsonPath(content + ".createdBy",
                            Is.is(file.getCreatedBy())))
                    .andExpect(MockMvcResultMatchers.jsonPath(content + ".created",
                            Is.is(file.getCreated().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))))
                    .andExpect(MockMvcResultMatchers.jsonPath(content + ".sizeBytes",
                            Is.is(file.getSizeBytes()), Long.class));
        }
    }

    @Test
    @WithMockUser
    void enumerateFilesEnforcesPermission() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Files.PATH))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void getFileHandlesUnknownFiles() throws Exception {
        BDDMockito.given(filesService.findFileRegistrationById(ArgumentMatchers.anyString()))
                .willThrow(FilesService.FileNotRegisteredException.class);
        mockMvc.perform(utils.get(FILE_PATH))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getFile() throws Exception {
        final FileRegistration file = files.get(0);

        BDDMockito.given(filesService.findFileRegistrationById(file.getId()))
                .willReturn(file);
        BDDMockito.given(filesService.readFile(file)).willReturn(FILE_CONTENTS);

        mockMvc.perform(utils.get(FILE_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(file.getMimeType()))
                .andExpect(MockMvcResultMatchers.content().bytes(FILE_CONTENTS));
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_FILES})
    void uploadFile() throws Exception {
        final FileRegistration file = files.get(0);

        BDDMockito.given(filesService.buildFileRegistration(ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(FILE_CONTENTS))).willReturn(file);
        BDDMockito.given(authService.getAuthenticatedUser())
                .willReturn(User.builder().id(USER_ID).build());

        mockMvc.perform(MockMvcRequestBuilders.multipart(RoutesConfig.Files.PATH)
                        .file(FILE_PARAM, FILE_CONTENTS).secure(true))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(filesService, Mockito.times(1))
                .saveFile(file, FILE_CONTENTS);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_FILES})
    void uploadFileHandlesEmptyUpload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(RoutesConfig.Files.PATH).secure(true))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_FILES})
    void uploadFileRejectsInvalidFiles() throws Exception {
        final FileRegistration file = files.get(0);

        BDDMockito.given(filesService.buildFileRegistration(ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(FILE_CONTENTS))).willReturn(file);
        BDDMockito.given(authService.getAuthenticatedUser())
                .willReturn(User.builder().id(USER_ID).build());
        BDDMockito.doThrow(IllegalArgumentException.class).when(filesService)
                .saveFile(file, FILE_CONTENTS);

        mockMvc.perform(MockMvcRequestBuilders.multipart(RoutesConfig.Files.PATH)
                        .file(FILE_PARAM, FILE_CONTENTS).secure(true))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    void uploadFileEnforcesPermission() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(RoutesConfig.Files.PATH)
                        .file(FILE_PARAM, FILE_CONTENTS).secure(true))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_FILES})
    void deleteFile() throws Exception {
        mockMvc.perform(utils.delete(FILE_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(filesService, Mockito.times(1))
                .deleteFile(FILE_ID);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_FILES})
    void deleteFileHandlesUnknownFiles() throws Exception {
        BDDMockito.doThrow(FilesService.FileNotRegisteredException.class)
                .when(filesService).deleteFile(FILE_ID);
        mockMvc.perform(utils.delete(FILE_PATH))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteFileEnforcesPermission() throws Exception {
        mockMvc.perform(utils.delete(FILE_PATH))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(filesService, queryParser, mapper, userDetailsService, rateLimitingService, authService);
    }

}
