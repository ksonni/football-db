package com.ksonni.footballdb.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Used by logback to append the current username in log messages.
 */
public class UserConverter extends ClassicConverter {

    @Override
    public String convert(final ILoggingEvent event) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            return "'" + auth.getName() + "'";
        }
        return "none";
    }

}
