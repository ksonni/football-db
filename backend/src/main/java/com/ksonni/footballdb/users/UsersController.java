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
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Users.PATH)
public class UsersController {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final QueryParser<User> queryParser;

    @GetMapping
    @RolesAllowed({ Permission.Code.VIEW_USERS })
    public Page<UserResponse> enumerateUsers(HttpServletRequest request) throws QueryParseException {
        Page<User> page = usersRepository.findAll(queryParser.parse(request.getQueryString()));
        return page.map(usersMapper::toUserResponse);
    }

}
