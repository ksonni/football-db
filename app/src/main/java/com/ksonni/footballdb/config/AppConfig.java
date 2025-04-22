package com.ksonni.footballdb.config;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.generated.ql.*;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.services.PlayersMapper;
import com.ksonni.footballdb.query.DefaultFilterParser;
import com.ksonni.footballdb.query.DefaultSortParser;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.SortParser;
import com.ksonni.footballdb.ratelimiting.IPRateLimitingService;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.users.services.DefaultAuthService;
import com.ksonni.footballdb.users.services.UsersMapper;
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

    @Value("${app.max-requests-per-window}")
    private Integer maxRequestsPerWindow;

    @Value("${app.throttling-window-minutes}")
    private Integer throttlingWindowMinutes;

    @Value("${app.max-filter-components}")
    private Integer maxFilterComponents;

    @Value("${app.max-sort-components}")
    private Integer maxSortComponents;

    @Value("${app.max-results-per-page}")
    private Integer maxQueryResults;

    /**
     * Supplies a GraphQL filter parser for players.
     *
     * @param playersMapper object mapper for player related types
     * @return FilterParser
     */
    @Bean
    public FilterParser<Player, QLPlayerFilter> playersFilterParser(final PlayersMapper playersMapper) {
        final FilterParser<Player, QLPlayerFilter> parser = new DefaultFilterParser<>(maxFilterComponents);

        parser.registerDecoder(QLSide.class, playersMapper::toSide);
        parser.registerDecoder(QLWorkRate.class, playersMapper::toWorkRate);
        parser.registerDecoder(QLPosition.class, playersMapper::toPosition);

        parser.assertDecodable(QLPlayerFilter.class);

        return parser;
    }

    /**
     * Supplies a GraphQL sort parser for players.
     *
     * @return SortParser
     */
    @Bean
    public SortParser<QLPlayerSort> playersSortParser() {
        return new DefaultSortParser<>(maxSortComponents, maxQueryResults);
    }

    /**
     * Supplies a GraphQL filter parser for leagues.
     *
     * @return FilterParser
     */
    @Bean
    public FilterParser<League, QLLeagueFilter> leaguesFilterParser() {
        final FilterParser<League, QLLeagueFilter> parser = new DefaultFilterParser<>(maxFilterComponents);
        parser.assertDecodable(QLLeagueFilter.class);
        return parser;
    }

    /**
     * Supplies a GraphQL sort parser for leagues.
     *
     * @return SortParser
     */
    @Bean
    public SortParser<QLLeagueSort> leaguesSortParser() {
        return new DefaultSortParser<>(maxSortComponents, maxQueryResults);
    }

    /**
     * Supplies a GraphQL filter parser for clubs.
     *
     * @return FilterParser
     */
    @Bean
    public FilterParser<Club, QLClubFilter> clubsFilterParser() {
        final FilterParser<Club, QLClubFilter> parser = new DefaultFilterParser<>(maxFilterComponents);
        parser.assertDecodable(QLClubFilter.class);
        return parser;
    }

    /**
     * Supplies a GraphQL sort parser for clubs.
     *
     * @return SortParser
     */
    @Bean
    public SortParser<QLClubSort> clubsSortParser() {
        return new DefaultSortParser<>(maxSortComponents, maxQueryResults);
    }


    /**
     * Supplies a GraphQL filter parser for files.
     *
     * @return FilterParser
     */
    @Bean
    public FilterParser<FileRegistration, QLFileRegistrationFilter> filesFilterParser() {
        final FilterParser<FileRegistration, QLFileRegistrationFilter> parser =
            new DefaultFilterParser<>(maxFilterComponents);
        parser.assertDecodable(QLFileRegistrationFilter.class);
        return parser;
    }

    /**
     * Supplies a GraphQL sort parser for files.
     *
     * @return SortParser
     */
    @Bean
    public SortParser<QLFileRegistrationSort> filesSortParser() {
        return new DefaultSortParser<>(maxSortComponents, maxQueryResults);
    }

    /**
     * Supplies a GraphQL filter parser for users.
     *
     * @param usersMapper object mapper for user related types
     * @return FilterParser
     */
    @Bean
    public FilterParser<User, QLUserFilter> usersFilterParser(final UsersMapper usersMapper) {
        final FilterParser<User, QLUserFilter> parser = new DefaultFilterParser<>(maxFilterComponents);
        parser.registerDecoder(QLRole.class, usersMapper::toRole);
        parser.assertDecodable(QLUserFilter.class);
        return parser;
    }

    /**
     * Supplies a GraphQL sort parser for users.
     *
     * @return SortParser
     */
    @Bean
    public SortParser<QLUserSort> usersSortParser() {
        return new DefaultSortParser<>(maxSortComponents, maxQueryResults);
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
        return new IPRateLimitingService(
            maxRequestsPerWindow,
            Duration.ofMinutes(throttlingWindowMinutes)
        );
    }

}
