package com.ksonni.footballdb.players;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.players.services.PlayersRepository;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayersController.class)
class PlayersControllerTests {

    @MockBean
    PlayersRepository playersRepository;

    @MockBean
    QueryParser<Player> queryParser;

    @Autowired
    MockMvc mockMvc;

    List<Player> players;

    @BeforeEach
    void setup() {
        players = Arrays.asList(
            Player.builder().id("id").fullName("Some player").attackingWorkRate(WorkRate.HIGH)
                    .defensiveWorkRate(WorkRate.LOW).preferredFoot(Side.LEFT).build(),
            Player.builder().id("id2").fullName("Some player 2").build()
        );
        Page<Player> pagedPlayers = new PageImpl<>(players,
                PageRequest.of(0, 2), 2);
        given(playersRepository.findAll(ArgumentMatchers.<Query<Player>>any()))
                .willReturn(pagedPlayers);
    }

    @AfterEach
    void tearDown() {
        reset(playersRepository, queryParser);
    }

    @Test
    void enumeratePlayers() throws Exception {
        mockMvc.perform(get(RoutesConfig.PLAYERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(players.size())))
                .andExpect(jsonPath("$.content[0].id", is(players.get(0).getId())))
                .andExpect(jsonPath("$.content[0].attackingWorkRate",
                        is(players.get(0).getAttackingWorkRate().getValue())))
                .andExpect(jsonPath("$.content[0].defensiveWorkRate",
                        is(players.get(0).getDefensiveWorkRate().getValue())))
                .andExpect(jsonPath("$.content[0].preferredFoot",
                        is(players.get(0).getPreferredFoot().getValue())))
                .andExpect(jsonPath("$.content[1].id", is(players.get(1).getId())));
    }

    @Test
    void enumeratePlayersInvalidQuery() throws Exception {
        given(queryParser.parse(anyString())).willThrow(QueryParseException.class);
        mockMvc.perform(get(RoutesConfig.PLAYERS_PATH + "?badquery:"))
                .andExpect(status().isBadRequest());
    }

}
