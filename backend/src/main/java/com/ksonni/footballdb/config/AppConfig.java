package com.ksonni.footballdb.config;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.services.PlayerQueryParser;
import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.ratelimiting.IPRateLimitingService;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.users.services.DefaultAuthService;
import com.ksonni.footballdb.users.services.UserQueryParser;
import com.ksonni.footballdb.utils.DocUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

import java.time.Duration;

/**
 * Supplies beans required by the app.
 */
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class AppConfig {

    private final BuildProperties buildProperties;

    @Value("${app.max-requests-per-min}")
    private Integer maxRequestsPerMin;

    /**
     * Supplies a QueryParser for clubs.
     *
     * @return QueryParser
     */
    @Bean
    public QueryParser<Club> clubsQueryParser() {
        return new DefaultQueryParser<>(Club.class);
    }

    /**
     * Supplies a QueryParser for players.
     *
     * @return QueryParser
     */
    @Bean
    public QueryParser<Player> playersQueryParser() {
        return new PlayerQueryParser();
    }

    /**
     * Supplies a QueryParser for leagues.
     *
     * @return QueryParser
     */
    @Bean
    public QueryParser<League> leaguesQueryParser() {
        return new DefaultQueryParser<>(League.class);
    }

    /**
     * Supplies a QueryParser for users.
     *
     * @return QueryParser
     */
    @Bean
    public QueryParser<User> usersQueryParser() {
        return new UserQueryParser();
    }

    /**
     * Supplies a QueryParser for files.
     *
     * @return QueryParser
     */
    @Bean
    public QueryParser<FileRegistration> filesQueryParser() {
        return new DefaultQueryParser<>(FileRegistration.class);
    }

    /**
     * Supplies an AuthService.
     *
     * @return AuthService
     */
    @Bean
    public AuthService authService() {
        return new DefaultAuthService();
    }

    /**
     * Configures Spring security to not use the ROLE_ prefix for permissions.
     *
     * @return GrantedAuthorityDefaults
     */
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    /**
     * Global config for Open API.
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI openAPIConfig() {
        final Components components = new Components();

        final Info info = new Info().title(DocUtils.MAIN_TITLE)
                .description(DocUtils.MAIN_DESCRIPTION)
                .version(buildProperties.getVersion());

        return new OpenAPI().components(components).info(info);
    }

    /**
     * Returns a RateLimitingService.
     *
     * @return RateLimitingService
     */
    @Bean
    public RateLimitingService rateLimitingService() {
        return new IPRateLimitingService(maxRequestsPerMin, Duration.ofMinutes(1));
    }

}
