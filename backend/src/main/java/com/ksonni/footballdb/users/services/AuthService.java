package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.users.domain.User;
import org.springframework.security.core.Authentication;

public interface AuthService {

    User getAuthenticatedUser();

    void setSessionAuth(Authentication auth);

    void clearSessionAuth();

}
