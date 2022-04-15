package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import com.ksonni.footballdb.users.domain.AuthMethod;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.UserResponse;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import com.ksonni.footballdb.utils.MockMvcUtils;
import com.ksonni.footballdb.utils.TestUtils;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(UsersController.class)
public class UsersControllerTests {

    private final MockMvcUtils utils = new MockMvcUtils();
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private UsersRepository usersRepository;
    @MockBean
    private UsersMapper usersMapper;
    @MockBean
    private QueryParser<User> queryParser;
    @MockBean
    private RateLimitingService rateLimitingService;
    @Autowired
    private MockMvc mockMvc;
    private List<User> users;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id("id").emailId("user@ksonni.com")
                .role(Role.USER).authMethod(AuthMethod.PASSWORD).build();
        users = Arrays.asList(user);

        BDDMockito.given(usersRepository.findAll(ArgumentMatchers.<Query<User>>any()))
                .willReturn(TestUtils.buildPage(users));
        BDDMockito.given(usersMapper.toUserResponse(user)).willReturn(
                UserResponse.builder().id(user.getId()).emailId(user.getEmailId())
                        .role(user.getRole()).authMethod(user.getAuthMethod()).build()
        );
        TestUtils.disableRateLimiting(rateLimitingService);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.VIEW_USERS})
    void enumerateUsers() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Users.PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(users.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", Is.is(user.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].emailId",
                        Is.is(user.getEmailId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].role",
                        Is.is(user.getRole().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].authMethod",
                        Is.is(user.getAuthMethod().getValue())));
    }

    @Test
    @WithMockUser
    void enumerateUsersErrorsWithoutPermission() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Users.PATH))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.VIEW_USERS})
    void handlesRateLimitsReached() throws Exception {
        TestUtils.mockRateLimitReached(rateLimitingService);
        mockMvc.perform(utils.get(RoutesConfig.Users.PATH))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(userDetailsService, usersRepository, queryParser,
                usersMapper, rateLimitingService);
    }

}
