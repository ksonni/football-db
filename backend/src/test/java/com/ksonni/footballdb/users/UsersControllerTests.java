package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.UserResponse;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import com.ksonni.footballdb.utils.MockMvcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
public class UsersControllerTests {

    @MockBean
    UserDetailsService userDetailsService;
    @MockBean
    UsersRepository usersRepository;
    @MockBean
    UsersMapper usersMapper;
    @MockBean
    QueryParser<User> queryParser;

    @Autowired
    MockMvc mockMvc;

    List<User> users;
    User user;

    private final MockMvcUtils utils = new MockMvcUtils();

    @BeforeEach
    void setup() {
        user = User.builder().id("id").emailId("user@ksonni.com")
                .role(Role.USER).build();
        users = Arrays.asList(user);

        Page<User> pagedUsers = new PageImpl<>(users,
                PageRequest.of(0, users.size()), users.size());

        given(usersRepository.findAll(ArgumentMatchers.<Query<User>>any()))
                .willReturn(pagedUsers);
        given(usersMapper.toUserResponse(user)).willReturn(
            UserResponse.builder().id(user.getId()).emailId(user.getEmailId())
                    .role(user.getRole()).build()
        );
    }

    @Test
    @WithMockUser(roles = { Permission.Code.VIEW_USERS })
    void enumerateUsers() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Users.PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(users.size())))
                .andExpect(jsonPath("$.content[0].id", is(user.getId())))
                .andExpect(jsonPath("$.content[0].emailId",
                        is(user.getEmailId())))
                .andExpect(jsonPath("$.content[0].role",
                        is(user.getRole().getValue())));
    }

    @Test
    @WithMockUser
    void enumerateUsersErrorsWithoutPermission() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Users.PATH))
                .andExpect(status().isForbidden());
    }

    @AfterEach
    void tearDown() {
        reset(userDetailsService, usersRepository, queryParser, usersMapper);
    }

}
