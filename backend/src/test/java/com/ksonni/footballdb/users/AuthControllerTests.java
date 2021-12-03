package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.RegisterUserRequest;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import com.ksonni.footballdb.utils.MockMvcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTests {

    @MockBean
    UsersMapper mapper;

    @MockBean
    PasswordEncoder encoder;

    @MockBean
    UsersRepository usersRepository;

    @Autowired
    MockMvc mockMvc;

    private final MockMvcUtils utils = new MockMvcUtils();

    private final User validUser = User.builder()
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

    private void testBadRegisterUserRequest(RegisterUserRequest request) throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Auth.REGISTER_PATH, request))
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    void tearDown() {
        reset(mapper, encoder, usersRepository);
    }

}
