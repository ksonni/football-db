package com.ksonni.footballdb.files;

import com.ksonni.footballdb.config.GraphQLConfig;
import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.files.services.FilesMapperImpl;
import com.ksonni.footballdb.files.services.FilesRepository;
import com.ksonni.footballdb.generated.ql.QLFileRegistration;
import com.ksonni.footballdb.generated.ql.QLFileRegistrationFilter;
import com.ksonni.footballdb.generated.ql.QLFileRegistrationSort;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.PageResult;
import com.ksonni.footballdb.query.SortParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;

@GraphQlTest(FilesControllerQL.class)
@Import({GraphQLConfig.class, FilesMapperImpl.class})
class FilesControllerQLTest {
    @Autowired
    private GraphQlTester graphQlTester;
    @MockitoBean
    private FilesRepository filesRepository;
    @MockitoBean
    private FilterParser<FileRegistration, QLFileRegistrationFilter> filesFilterParser;
    @MockitoBean
    private SortParser<QLFileRegistrationSort> filesSortParser;

    @Test
    void testFilesPath() {
        // Setup
        final var files = Arrays.asList(
            FileRegistration.builder().id("id").name("Some file").build(),
            FileRegistration.builder().id("id2").name("Some file 2").build()
        );
        final var result = new PageResult<>(files, files.size(), files.size(), files.size());
        BDDMockito.given(filesRepository.findAllResults(Mockito.any(), Mockito.any())).willReturn(result);

        // Execute
        final var contentPath = "files.content";
        final var response = graphQlTester.document("query { files { content { id name } } }")
            .execute().path(contentPath).entityList(QLFileRegistration.class).get();

        // Verify
        Assertions.assertEquals(files.size(), response.size());
        for (int i = 0; i < files.size(); i++) {
            Assertions.assertEquals(files.get(i).getId(), response.get(i).getId());
            Assertions.assertEquals(files.get(i).getName(), response.get(i).getName());
        }
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(filesRepository, filesFilterParser, filesSortParser);
    }
}
