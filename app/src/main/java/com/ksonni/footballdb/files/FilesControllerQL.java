package com.ksonni.footballdb.files;

import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.files.services.FilesMapper;
import com.ksonni.footballdb.files.services.FilesRepository;
import com.ksonni.footballdb.generated.ql.QLFileRegistrationFilter;
import com.ksonni.footballdb.generated.ql.QLFileRegistrationPage;
import com.ksonni.footballdb.generated.ql.QLFileRegistrationSort;
import com.ksonni.footballdb.generated.ql.QLPagination;
import com.ksonni.footballdb.qlquery.FilterParseException;
import com.ksonni.footballdb.qlquery.FilterParser;
import com.ksonni.footballdb.qlquery.SortParseException;
import com.ksonni.footballdb.qlquery.SortParser;
import com.ksonni.footballdb.users.domain.Permission;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

/**
 * GraphQL mappings to query files.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class FilesControllerQL {

    private final FilesMapper filesMapper;
    private final FilesRepository filesRepository;
    private final FilterParser<FileRegistration, QLFileRegistrationFilter> filesFilterParser;
    private final SortParser<QLFileRegistrationSort> filesSortParser;

    /**
     * Query files with filtering, sorting & pagination.
     *
     * @param filter filter to select files
     * @param sort specifies sort order for results
     * @param page configures pagination
     * @return paginated list of files matching the filter.
     */
    @QueryMapping
    @Transactional(readOnly = true)
    @RolesAllowed({Permission.Code.MANAGE_FILES})
    public QLFileRegistrationPage files(
        @Argument final QLFileRegistrationFilter filter,
        @Argument final QLFileRegistrationSort sort,
        @Argument final QLPagination page
    ) throws FilterParseException, SortParseException {
        final var results = filesRepository.findAllResults(
            filesFilterParser.parse(filter).orElse(null),
            filesSortParser.parse(sort, page)
        );
        log.info("returning {} files", results.content().size());
        return filesMapper.toQLPage(results);
    }

}
