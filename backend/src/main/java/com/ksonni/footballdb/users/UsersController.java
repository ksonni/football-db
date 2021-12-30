package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.UserResponse;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Users.PATH)
@UsersControllerDoc
public class UsersController {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final QueryParser<User> queryParser;

    /**
     * Query users.
     *
     * @param request HTTP request
     * @return Paginated list of users
     * @throws QueryParseException If the query is not valid
     */
    @GetMapping
    @RolesAllowed({Permission.Code.VIEW_USERS})
    @Transactional(readOnly = true)
    @EnumerateUsersDoc
    public Page<UserResponse> enumerateUsers(final HttpServletRequest request) throws QueryParseException {
        final String query = request.getQueryString();
        log.info("Processing query: {}", query);
        return usersRepository.findAll(queryParser.parse(query)).map(usersMapper::toUserResponse);
    }

}
