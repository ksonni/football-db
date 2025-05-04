package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.GraphQLConfig;
import com.ksonni.footballdb.generated.ql.QLLeague;
import com.ksonni.footballdb.generated.ql.QLLeagueFilter;
import com.ksonni.footballdb.generated.ql.QLLeagueSort;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.services.LeaguesMapperImpl;
import com.ksonni.footballdb.leagues.services.LeaguesRepository;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.PageResult;
import com.ksonni.footballdb.query.SortParser;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import com.ksonni.footballdb.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.Optional;

@GraphQlTest(LeaguesControllerQL.class)
@Import({GraphQLConfig.class, LeaguesMapperImpl.class})
class LeaguesControllerQLTest {
    @Autowired
    private GraphQlTester graphQlTester;
    @MockitoBean
    private LeaguesRepository leaguesRepository;
    @MockitoBean
    private FilterParser<League, QLLeagueFilter> leaguesFilterParser;
    @MockitoBean
    private SortParser<QLLeagueSort> leaguesSortParser;
    @MockitoBean
    private RateLimitingService rateLimitingService;

    private final String leaguesPath = "leagues";
    private final String leaguesQuery = "query { leagues { content { id name } } }";

    @BeforeEach
    void setup() {
        TestUtils.disableRateLimiting(rateLimitingService);
    }

    @Test
    void testLeaguesPath() {
        // Setup
        final var leagues = Arrays.asList(
            League.builder().id("id").name("Some league").build(),
            League.builder().id("id2").name("Some league 2").build()
        );
        final var result = new PageResult<>(leagues, leagues.size(), leagues.size(), leagues.size());
        BDDMockito.given(leaguesRepository.findAllResults(Mockito.any(), Mockito.any())).willReturn(result);

        // Execute
        final var contentPath = "leagues.content";
        final var response = graphQlTester.document(leaguesQuery)
            .execute().path(contentPath).entityList(QLLeague.class).get();

        // Verify
        Assertions.assertEquals(leagues.size(), response.size());
        for (int i = 0; i < leagues.size(); i++) {
            Assertions.assertEquals(leagues.get(i).getId(), response.get(i).getId());
            Assertions.assertEquals(leagues.get(i).getName(), response.get(i).getName());
        }
    }

    @Test
    void testLeaguePath() {
        // Setup
        final var league = League.builder().id("id").name("Some league").build();
        BDDMockito.given(leaguesRepository.findById("id")).willReturn(Optional.of(league));

        // Execute
        final var response = graphQlTester.document("query { league(id: \"id\") { id name } }")
            .execute().path("league").entity(QLLeague.class).get();

        // Verify
        Assertions.assertEquals(league.getId(), response.getId());
        Assertions.assertEquals(league.getName(), response.getName());
    }

    @Test
    void testEnforcesRateLimits() {
        // Setup
        TestUtils.mockRateLimitReached(rateLimitingService);

        // Execute
        graphQlTester.document(leaguesQuery)
            .execute().errors()
            .expect(e -> e.getErrorType().equals(graphql.ErrorType.DataFetchingException)).verify()
            .path(leaguesPath).pathDoesNotExist();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(leaguesRepository, leaguesFilterParser, leaguesSortParser, rateLimitingService);
    }
}
