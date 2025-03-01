package com.ksonni.footballdb.config;

import com.ksonni.footballdb.ratelimiting.RateLimitingInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
public class WebSecurityConfig implements WebMvcConfigurer {

    private static final int HTTP_PORT = 80;
    private static final int HTTPS_PORT = 443;

    private final RateLimitingInterceptor rateLimitInterceptor;

    @Bean
    SecurityFilterChain filterChain(final HttpSecurity security) throws Exception {
        HttpSecurity http = security;
        http = requireHttps(http);
        http = http.securityContext(c -> c.requireExplicitSave(false));
        http = http.csrf(c -> c.disable());
        http = configureUnauthenticatedRoutes(http);
        http = enableAuthentication(http);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
        final UserDetailsService userDetailsService,
        final PasswordEncoder passwordEncoder
    ) {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        final List<RoutesConfig.UnauthenticatedRoute> corsRoutes = RoutesConfig.UNAUTHENTICATED_ROUTES
                .stream().filter(route -> route.isCrossOrigin()).collect(Collectors.toList());

        for (var route : corsRoutes) {
            registry.addMapping(route.getPattern()).allowedMethods(route.getMethod().name());
        }
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor);
    }

    private HttpSecurity configureUnauthenticatedRoutes(final HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(a -> {
            var auth = a;
            for (var route : RoutesConfig.UNAUTHENTICATED_ROUTES) {
                auth = auth.requestMatchers(route.getMethod(), route.getPattern()).permitAll();
            }
        });
    }

    private HttpSecurity enableAuthentication(final HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(r -> r.anyRequest().authenticated());
    }

    private HttpSecurity requireHttps(final HttpSecurity http) throws Exception {
        return http.portMapper(p -> p.http(HTTP_PORT).mapsTo(HTTPS_PORT))
                .requiresChannel(c -> c.anyRequest().requiresSecure());
    }

}
