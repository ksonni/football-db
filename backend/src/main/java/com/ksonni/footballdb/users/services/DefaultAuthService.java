package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.users.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class DefaultAuthService implements AuthService {

    @Override
    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public void setSessionAuth(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public void clearSessionAuth() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}
