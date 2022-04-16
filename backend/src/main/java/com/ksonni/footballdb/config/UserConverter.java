package com.ksonni.footballdb.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.ksonni.footballdb.users.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * Used by logback to append the current username in log messages.
 */
public class UserConverter extends ClassicConverter {

    @Override
    public String convert(final ILoggingEvent event) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return "none";
        }
        final var user = auth.getPrincipal();
        if (user instanceof OidcUser) {
            return "'" + ((OidcUser) user).getEmail() + "'";
        }
        if (user instanceof User) {
            return "'" + ((User) user).getEmailId() + "'";
        }
        return "none";
    }

}
