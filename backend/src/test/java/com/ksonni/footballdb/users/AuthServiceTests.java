package com.ksonni.footballdb.users;

import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.users.services.DefaultAuthService;
import com.ksonni.footballdb.users.services.UsersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @MockBean
    private UsersRepository usersRepository;

    private AuthService authService;

    @BeforeEach
    void setup() {
        authService = new DefaultAuthService(usersRepository);
    }

    @Test
    void testGetDefaultRoleWhenUsersExist() {
        BDDMockito.given(usersRepository.findFirstByOrderByEmailIdAsc())
                .willReturn(User.builder().build());
        Assertions.assertEquals(Role.USER, authService.getDefaultRole());
    }

    @Test
    void testGetDefaultRoleWhenNoUsersExist() {
        BDDMockito.given(usersRepository.findFirstByOrderByEmailIdAsc())
                .willReturn(null);
        Assertions.assertEquals(Role.ADMIN, authService.getDefaultRole());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(usersRepository);
    }

}
