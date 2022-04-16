package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private final UsersRepository usersRepository;

    @Override
    public User getAuthenticatedUser() {
        final var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        if (principal instanceof OidcUser) {
            return usersRepository.findByEmailId(((OidcUser) principal).getEmail());
        }
        return null;
    }

    @Override
    public void setSessionAuth(final Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public void clearSessionAuth() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Override
    public Role getDefaultRole() {
        final boolean isFirstUser = usersRepository.findFirstByOrderByEmailIdAsc() == null;
        return isFirstUser ? Role.ADMIN : Role.USER;
    }

}
