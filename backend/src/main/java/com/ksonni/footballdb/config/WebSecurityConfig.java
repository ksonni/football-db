package com.ksonni.footballdb.config;

import com.ksonni.footballdb.ratelimiting.RateLimitingInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private static final int HTTP_PORT = 80;
    private static final int HTTPS_PORT = 443;

    private final UserDetailsService userDetailsService;
    private final RateLimitingInterceptor rateLimitInterceptor;

    @Override
    protected void configure(final HttpSecurity security) throws Exception {
        HttpSecurity http = security;
        http = requireHttps(http);
        http = http.cors().and();
        http = http.csrf().disable();
        http = configureUnauthenticatedRoutes(http);
        http = enableAuthentication(http);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    /**
     * Configures the PasswordEncoder to use for authentication and registration.
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
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
        var auth = http.authorizeRequests();
        for (var route : RoutesConfig.UNAUTHENTICATED_ROUTES) {
            auth = auth.antMatchers(route.getMethod(), route.getPattern()).permitAll();
        }
        return auth.and();
    }

    private HttpSecurity enableAuthentication(final HttpSecurity http) throws Exception {
        return http.authorizeRequests().anyRequest().authenticated().and();
    }

    private HttpSecurity requireHttps(final HttpSecurity http) throws Exception {
        return http.portMapper().http(HTTP_PORT).mapsTo(HTTPS_PORT).and()
                .requiresChannel().anyRequest().requiresSecure().and();
    }

}
