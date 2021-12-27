package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.LoginRequest;
import com.ksonni.footballdb.users.dto.RegisterUserRequest;
import com.ksonni.footballdb.users.dto.UserResponse;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import com.ksonni.footballdb.utils.MockMvcUtils;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthController.class)
class AuthControllerTests {

    private final MockMvcUtils utils = new MockMvcUtils();
    private final User validUser = User.builder()
            .id("id")
            .emailId("test@ksonni.com")
            .password("sdfsdfsdf*&£1")
            .build();
    private final RegisterUserRequest validRegisterRequest = RegisterUserRequest.builder()
            .emailId(validUser.getEmailId())
            .password(validUser.getPassword())
            .build();
    @MockBean
    private UsersMapper mapper;
    @MockBean
    private PasswordEncoder encoder;
    @MockBean
    private UsersRepository usersRepository;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthService authService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerUserWhenThereArePreExistingUsers() throws Exception {
        final User preExistingUser = User.builder().emailId("something@ksonni.com").build();
        BDDMockito.given(usersRepository.findFirstByOrderByEmailIdAsc()).willReturn(preExistingUser);

        final ArgumentCaptor<User> createdUser = testSuccessfulUserRegistration();

        Assertions.assertEquals(Role.USER, createdUser.getValue().getRole());
    }

    @Test
    void registerUserWhenThereAreNoPreExistingUsers() throws Exception {
        BDDMockito.given(usersRepository.findFirstByOrderByEmailIdAsc()).willReturn(null);

        final ArgumentCaptor<User> createdUser = testSuccessfulUserRegistration();

        Assertions.assertEquals(Role.ADMIN, createdUser.getValue().getRole());
    }

    @Test
    void registerUserRejectsDuplicates() throws Exception {
        BDDMockito.given(mapper.toUser(ArgumentMatchers.any(RegisterUserRequest.class)))
                .willReturn(validUser);
        BDDMockito.given(usersRepository.findByEmailId(ArgumentMatchers.anyString()))
                .willReturn(validUser);

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.REGISTER_PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void registerUserValidatesRequest() throws Exception {
        testBadRegisterUserRequest(RegisterUserRequest.builder()
                .emailId("bad email")
                .password(validUser.getPassword())
                .build());

        testBadRegisterUserRequest(RegisterUserRequest.builder()
                .emailId(validUser.getEmailId())
                .password("too short")
                .build());
    }

    @Test
    void loginUserRequest() throws Exception {
        final Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(validUser);
        Mockito.when(auth.isAuthenticated()).thenReturn(true);

        BDDMockito.given(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(auth);

        final LoginRequest request = LoginRequest.builder()
                .emailId(validUser.getEmailId())
                .password(validUser.getPassword()).build();

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.LOGIN_PATH, request))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(authenticationManager, Mockito.times(1))
                .authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(authService, Mockito.times(1))
                .setSessionAuth(ArgumentMatchers.any(Authentication.class));
    }

    @Test
    void badCredentialsLoginRequest() throws Exception {
        BDDMockito.given(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(BadCredentialsException.class);

        final LoginRequest request = LoginRequest.builder()
                .emailId(validUser.getEmailId())
                .password("Bad password").build();

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.LOGIN_PATH, request))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        Mockito.verify(authenticationManager, Mockito.times(1))
                .authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @WithMockUser
    void meRequest() throws Exception {
        BDDMockito.given(authService.getAuthenticatedUser()).willReturn(validUser);
        BDDMockito.given(mapper.toUserResponse(ArgumentMatchers.any(User.class))).willReturn(
                UserResponse.builder().id(validUser.getId())
                        .emailId(validUser.getEmailId()).build()
        );

        mockMvc.perform(utils.get(RoutesConfig.Auth.ME_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Is.is(validUser.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.emailId", Is.is(validUser.getEmailId())));
    }

    @Test
    void meRequestUnauthenticated() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Auth.ME_PATH))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void logoutRequest() throws Exception {
        mockMvc.perform(utils.post(RoutesConfig.Auth.LOGOUT_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(authService, Mockito.times(1)).clearSessionAuth();
    }

    private void testBadRegisterUserRequest(final RegisterUserRequest request) throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.REGISTER_PATH, request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private ArgumentCaptor<User> testSuccessfulUserRegistration() throws Exception {
        BDDMockito.given(mapper.toUser(ArgumentMatchers.any(RegisterUserRequest.class)))
                .willReturn(validUser);

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.REGISTER_PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(encoder, Mockito.times(1))
                .encode(ArgumentMatchers.anyString());
        final ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        Mockito.verify(usersRepository).save(argument.capture());

        return argument;
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mapper, encoder, usersRepository, userDetailsService, authService);
    }

}
