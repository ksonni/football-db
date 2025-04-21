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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Auth.PATH)
@Tag(name = "Auth", description = "User registration and auth")
public class AuthController {

    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    /**
     * Sign up a new user.
     *
     * @param request Register request DTO
     */
    @PostMapping(value = RoutesConfig.Auth.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @Operation(summary = "Register a new user account")
    public void registerUser(final @Valid @RequestBody RegisterUserRequest request) {
        final User user = usersMapper.toUser(request);

        if (usersRepository.findByEmailId(user.getEmailId()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address already in use");
        }

        final boolean isFirstUser = usersRepository.findFirstByOrderByEmailIdAsc() == null;
        final String password = passwordEncoder.encode(request.getPassword());

        user.setPassword(password);
        user.setId(UUID.randomUUID().toString());
        user.setRole(isFirstUser ? Role.ADMIN : Role.USER);

        usersRepository.save(user);
        log.info("created user {} with role {}", user.getEmailId(), user.getRole());
    }

    /**
     * Login user.
     *
     * @param request Login user DTO
     */
    @PostMapping(value = RoutesConfig.Auth.LOGIN)
    @Transactional(readOnly = true)
    @Operation(summary = "Login to start a new session")
    public void loginUser(final @Valid @RequestBody LoginRequest request) {
        final var token = new UsernamePasswordAuthenticationToken(request.getEmailId(), request.getPassword());

        final Authentication auth;
        try {
            auth = authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        authService.setSessionAuth(auth);
        log.info("logged in");
    }

    /**
     * Logout current user.
     */
    @PostMapping(value = RoutesConfig.Auth.LOGOUT)
    @Operation(summary = "Logout")
    public void logoutUser() {
        log.info("logging out");
        authService.clearSessionAuth();
    }

    /**
     * Get details about the current user.
     *
     * @return user details
     */
    @GetMapping(value = RoutesConfig.Auth.ME)
    @Transactional(readOnly = true)
    @Operation(summary = "Fetch details about the logged in user")
    public UserResponse getMe() {
        final User user = authService.getAuthenticatedUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return usersMapper.toUserResponse(user);
    }

}
