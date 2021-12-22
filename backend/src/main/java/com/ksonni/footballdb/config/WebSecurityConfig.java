package com.ksonni.footballdb.config;

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

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(
    jsr250Enabled = true,
    prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        HttpSecurity http = security;
        http = http.cors().and();
        http = http.csrf().disable();
        http = configureUnauthenticatedRoutes(http);
        http = enableAuthentication(http);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    private HttpSecurity configureUnauthenticatedRoutes(HttpSecurity http) throws Exception {
        var auth = http.authorizeRequests();
        for (var route: RoutesConfig.UNAUTHENTICATED_ROUTES) {
            auth = auth.antMatchers(route.getMethod(), route.getPattern()).permitAll();
        }
        return auth.and();
    }

    private HttpSecurity enableAuthentication(HttpSecurity http) throws Exception {
        return http.authorizeRequests().anyRequest().authenticated().and();
    }

}
