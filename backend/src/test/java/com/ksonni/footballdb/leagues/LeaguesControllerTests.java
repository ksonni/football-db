package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.RoutesConfig;
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

@WebMvcTest(LeaguesController.class)
class LeaguesControllerTests {

    @MockBean
    LeaguesRepository leaguesRepository;

    @MockBean
    QueryParser<League> queryParser;

    @Autowired
    MockMvc mockMvc;

    List<League> leagues;

    @BeforeEach
    void setup() {
        leagues = Arrays.asList(
                League.builder().id("id").name("Some league").build(),
                League.builder().id("id2").name("Some league 2").build()
        );
        Page<League> pagedLeagues = new PageImpl<>(leagues,
                PageRequest.of(0, 2), 2);
        given(leaguesRepository.findAll(ArgumentMatchers.<Query<League>>any()))
                .willReturn(pagedLeagues);
    }

    @AfterEach
    void tearDown() {
        reset(leaguesRepository, queryParser);
    }

    @Test
    void enumerateLeagues() throws Exception {
        mockMvc.perform(get(RoutesConfig.LEAGUES_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(leagues.size())))
                .andExpect(jsonPath("$.content[0].id", is(leagues.get(0).getId())))
                .andExpect(jsonPath("$.content[1].id", is(leagues.get(1).getId())));
    }

    @Test
    void enumerateLeaguesInvalidQuery() throws Exception {
        given(queryParser.parse(anyString())).willThrow(QueryParseException.class);
        mockMvc.perform(get(RoutesConfig.LEAGUES_PATH + "?badquery:"))
                .andExpect(status().isBadRequest());
    }

}
