package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private final UsersRepository usersRepository;

    @Override
    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
