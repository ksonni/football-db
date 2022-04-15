package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import org.springframework.security.core.Authentication;

public interface AuthService {

    /**
     * Get currently authenticated user.
     *
     * @return User
     */
    User getAuthenticatedUser();

    /**
     * Initialize an auth session.
     *
     * @param auth Result of authentication process
     */
    void setSessionAuth(Authentication auth);

    /**
     * Clear the user auth session.
     */
    void clearSessionAuth();

    /**
     * Decides the role to assign to new users.
     *
     * @return role to assign to a new user
     */
    Role getDefaultRole();
}
