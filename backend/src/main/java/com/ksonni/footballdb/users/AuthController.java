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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Auth.PATH)
public class AuthController {

    private final UsersMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @PostMapping(value = RoutesConfig.Auth.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public void registerUser(@Valid @RequestBody RegisterUserRequest request) {
        User user = mapper.toUser(request);

        if (usersRepository.findByEmailId(user.getEmailId()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address already in use");
        }

        boolean isFirstUser = usersRepository.findFirstByOrderByEmailIdAsc() == null;
        String password = passwordEncoder.encode(request.getPassword());

        user.setPassword(password);
        user.setId(UUID.randomUUID().toString());
        user.setRole(isFirstUser ? Role.ADMIN : Role.USER);

        usersRepository.save(user);
    }

    @PostMapping(value = RoutesConfig.Auth.LOGIN)
    @Transactional(readOnly = true)
    public void loginUser(@Valid @RequestBody LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.getEmailId(), request.getPassword());

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        authService.setSessionAuth(auth);
    }

    @PostMapping(value = RoutesConfig.Auth.LOGOUT)
    public void logoutUser(HttpServletRequest request) {
        authService.clearSessionAuth();
    }

    @GetMapping(value = RoutesConfig.Auth.ME)
    @Transactional(readOnly = true)
    public UserResponse getMe() {
        User user = authService.getAuthenticatedUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return mapper.toUserResponse(user);
    }

}
