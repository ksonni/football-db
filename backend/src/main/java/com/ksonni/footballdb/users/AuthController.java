package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.RegisterUserRequest;
import com.ksonni.footballdb.users.services.UsersMapper;
import com.ksonni.footballdb.users.services.UsersRepository;
import com.ksonni.footballdb.utils.HttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Auth.PATH)
public class AuthController {

    private final UsersMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    @PostMapping(value = RoutesConfig.Auth.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody RegisterUserRequest request) throws HttpException {
        User user = mapper.toUser(request);

        if (usersRepository.findByEmailId(user.getEmailId()) != null) {
            throw new HttpException(HttpStatus.CONFLICT, "Email address already in use");
        }

        String password = passwordEncoder.encode(request.getPassword());
        user.setPassword(password);
        user.setId(UUID.randomUUID().toString());

        usersRepository.save(user);
    }

}
