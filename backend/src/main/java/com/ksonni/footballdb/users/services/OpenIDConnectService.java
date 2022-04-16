package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.users.domain.AuthMethod;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenIDConnectService extends OidcUserService {

    private final AuthService authService;
    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public OidcUser loadUser(final OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        final OidcUser oidcUser = super.loadUser(userRequest);
        User user = usersRepository.findByEmailId(oidcUser.getEmail());

        if (user == null) {
            user = registerUser(oidcUser);
        } else if (user.getAuthMethod() != AuthMethod.OIDC) {
            throw new InternalAuthenticationServiceException("Oauth method is not supported for this user!");
        }
        return new DefaultOidcUser(user.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private User registerUser(final OidcUser oidcUser) {
        final User user = new User();
        user.setId(StringUtils.uuid());
        user.setEmailId(oidcUser.getEmail());
        user.setRole(authService.getDefaultRole());
        user.setAuthMethod(AuthMethod.OIDC);
        usersRepository.save(user);
        return user;
    }

}
