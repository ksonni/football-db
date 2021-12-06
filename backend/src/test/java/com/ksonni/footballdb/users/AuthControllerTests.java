package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.LoginRequest;
import com.ksonni.footballdb.users.dto.RegisterUserRequest;
import com.ksonni.footballdb.users.dto.UserResponse;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import com.ksonni.footballdb.utils.MockMvcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTests {

    @MockBean
    UsersMapper mapper;
    @MockBean
    PasswordEncoder encoder;
    @MockBean
    UsersRepository usersRepository;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    UserDetailsService userDetailsService;
    @MockBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

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

    @Test
    void registerUserSucceeds() throws Exception {
        given(mapper.toUser(any(RegisterUserRequest.class)))
                .willReturn(validUser);

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.REGISTER_PATH, validRegisterRequest))
                .andExpect(status().isCreated());

        verify(encoder, times(1)).encode(anyString());
        verify(usersRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUserRejectsDuplicates() throws Exception {
        given(mapper.toUser(any(RegisterUserRequest.class)))
                .willReturn(validUser);
        given(usersRepository.findByEmailId(anyString()))
                .willReturn(validUser);

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.REGISTER_PATH, validRegisterRequest))
                .andExpect(status().isConflict());
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
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(validUser);
        when(auth.isAuthenticated()).thenReturn(true);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(auth);

        LoginRequest request = LoginRequest.builder()
                .emailId(validUser.getEmailId())
                .password(validUser.getPassword()).build();

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.LOGIN_PATH, request))
                .andExpect(status().isOk());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, times(1)).setSessionAuth(any(Authentication.class));
    }

    @Test
    void badCredentialsLoginRequest() throws Exception {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(BadCredentialsException.class);

        LoginRequest request = LoginRequest.builder()
                .emailId(validUser.getEmailId())
                .password("Bad password").build();

        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.LOGIN_PATH, request))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @WithMockUser
    void meRequest() throws Exception {
        given(authService.getAuthenticatedUser()).willReturn(validUser);
        given(mapper.toUserResponse(any(User.class))).willReturn(
            UserResponse.builder().id(validUser.getId())
                    .emailId(validUser.getEmailId()).build()
        );

        mockMvc.perform(get(RoutesConfig.Auth.ME_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(validUser.getId())))
                .andExpect(jsonPath("$.emailId", is(validUser.getEmailId())));
    }

    @Test
    void meRequestUnauthenticated() throws Exception {
        mockMvc.perform(get(RoutesConfig.Auth.ME_PATH)).andExpect(status().isForbidden());
    }

    @Test
    void logoutRequest() throws Exception {
        mockMvc.perform(post(RoutesConfig.Auth.LOGOUT_PATH))
                .andExpect(status().isOk());

        verify(authService, times(1)).clearSessionAuth();
    }

    private void testBadRegisterUserRequest(RegisterUserRequest request) throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.REGISTER_PATH, request))
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    void tearDown() {
        reset(mapper, encoder, usersRepository, userDetailsService, authService);
    }

}
