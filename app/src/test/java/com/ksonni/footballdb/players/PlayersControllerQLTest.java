package com.ksonni.footballdb.players;

import com.ksonni.footballdb.GlobalExceptionHandler;
import com.ksonni.footballdb.config.GraphQLConfig;
import com.ksonni.footballdb.generated.ql.QLPlayer;
import com.ksonni.footballdb.generated.ql.QLPlayerFilter;
import com.ksonni.footballdb.generated.ql.QLPlayerSort;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.services.PlayersMapperImpl;
import com.ksonni.footballdb.players.services.PlayersRepository;
import com.ksonni.footballdb.query.FilterParseException;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.PageResult;
import com.ksonni.footballdb.query.SortParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.Optional;

@GraphQlTest(PlayersControllerQL.class)
@Import({GraphQLConfig.class, PlayersMapperImpl.class, GlobalExceptionHandler.class})
public class PlayersControllerQLTest {
    @Autowired
    private GraphQlTester graphQlTester;
    @MockitoBean
    private PlayersRepository playersRepository;
    @MockitoBean
    private FilterParser<Player, QLPlayerFilter> playersFilterParser;
    @MockitoBean
    private SortParser<QLPlayerSort> playersSortParser;

    private final String playersPath = "players";
    private final String playersQuery = "query { players { content { id fullName } } }";

    @Test
    void testPlayersPath() {
        // Setup
        final var players = Arrays.asList(
            Player.builder().id("id").fullName("Some player").build(),
            Player.builder().id("id2").fullName("Some player 2").build()
        );
        final var result = new PageResult<>(players, players.size(), players.size(), players.size());
        BDDMockito.given(playersRepository.findAllResults(Mockito.any(), Mockito.any())).willReturn(result);

        // Execute
        final var contentPath = playersPath + ".content";
        final var response = graphQlTester.document(playersQuery)
            .execute().path(contentPath).entityList(QLPlayer.class).get();

        // Verify
        Assertions.assertEquals(players.size(), response.size());
        for (int i = 0; i < players.size(); i++) {
            Assertions.assertEquals(players.get(i).getId(), response.get(i).getId());
            Assertions.assertEquals(players.get(i).getFullName(), response.get(i).getFullName());
        }
    }

    @Test
    void testPlayersPathInvalidFilter() throws FilterParseException {
        // Setup
        BDDMockito.given(playersFilterParser.parse(Mockito.any()))
            .willThrow(FilterParseException.class);

        // Execute
        graphQlTester.document(playersQuery)
            .execute().errors()
            .expect(e -> e.getPath().equals(playersPath)
                && e.getErrorType().equals(ErrorType.BAD_REQUEST)).verify()
            .path(playersPath).valueIsNull();
    }

    @Test
    void testPlayersPathInvalidSort() throws FilterParseException {
        // Setup
        BDDMockito.given(playersFilterParser.parse(Mockito.any()))
            .willThrow(FilterParseException.class);

        // Execute
        graphQlTester.document(playersQuery)
            .execute().errors()
            .expect(e -> e.getPath().equals(playersPath)
                && e.getErrorType().equals(ErrorType.BAD_REQUEST)).verify()
            .path(playersPath).valueIsNull();
    }

    @Test
    void testPlayerPath() {
        // Setup
        final var player = Player.builder().id("id").fullName("Some player").build();
        BDDMockito.given(playersRepository.findById("id")).willReturn(Optional.of(player));

        // Execute
        final var response = graphQlTester.document("query { player(id: \"id\") { id fullName } }")
            .execute().path("player").entity(QLPlayer.class).get();

        // Verify
        Assertions.assertEquals(player.getId(), response.getId());
        Assertions.assertEquals(player.getFullName(), response.getFullName());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(playersRepository, playersFilterParser, playersSortParser);
    }
}
