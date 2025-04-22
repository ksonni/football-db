package com.ksonni.footballdb.users;

import com.ksonni.footballdb.generated.ql.*;
import com.ksonni.footballdb.query.FilterParseException;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.SortParseException;
import com.ksonni.footballdb.query.SortParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * GraphQL mappings to query users.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class UsersControllerQL {

    private final UsersMapper usersMapper;
    private final UsersRepository usersRepository;
    private final FilterParser<User, QLUserFilter> usersFilterParser;
    private final SortParser<QLUserSort> usersSortParser;

    /**
     * Get a user by id.
     *
     * @param id id of the user
     * @return user with the id
     */
    @QueryMapping
    @Transactional(readOnly = true)
    @RolesAllowed(Permission.Code.VIEW_USERS)
    public Optional<QLUser> user(@Argument final String id) {
        log.info("finding user {}", id);
        return usersRepository.findById(id).map(usersMapper::toUserQL);
    }

    /**
     * Query users with filtering, sorting & pagination.
     *
     * @param filter filter to select users
     * @param sort specifies sort order for results
     * @param page configures pagination
     * @return paginated list of users matching the filter.
     */
    @QueryMapping
    @Transactional(readOnly = true)
    @RolesAllowed(Permission.Code.VIEW_USERS)
    public QLUserPage users(
        @Argument final QLUserFilter filter,
        @Argument final QLUserSort sort,
        @Argument final QLPagination page
    ) throws FilterParseException, SortParseException {
        final var results = usersRepository.findAllResults(
            usersFilterParser.parse(filter).orElse(null),
            usersSortParser.parse(sort, page)
        );
        log.info("returning {} users", results.content().size());
        return usersMapper.toQLPage(results);
    }

}
