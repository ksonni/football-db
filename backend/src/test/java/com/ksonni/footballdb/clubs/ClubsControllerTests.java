package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
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

@WebMvcTest(ClubsController.class)
class ClubsControllerTests {

    @MockBean
    ClubsRepository clubsRepository;

    @MockBean
    QueryParser<Club> queryParser;

    @Autowired
    MockMvc mockMvc;

    List<Club> clubs;

    @BeforeEach
    void setup() {
        clubs = Arrays.asList(
            Club.builder().id("id").name("Some club").build(),
            Club.builder().id("id2").name("Some club 2").build()
        );
        Page<Club> pagedClubs = new PageImpl<>(clubs,
                PageRequest.of(0, 2), 2);
        given(clubsRepository.findAll(ArgumentMatchers.<Query<Club>>any()))
                .willReturn(pagedClubs);
    }

    @AfterEach
    void tearDown() {
        reset(clubsRepository, queryParser);
    }

    @Test
    void enumerateClubs() throws Exception {
        mockMvc.perform(get(RoutesConfig.CLUBS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(clubs.size())))
                .andExpect(jsonPath("$.content[0].id", is(clubs.get(0).getId())))
                .andExpect(jsonPath("$.content[1].id", is(clubs.get(1).getId())));
    }

    @Test
    void enumerateClubsInvalidQuery() throws Exception {
        given(queryParser.parse(anyString())).willThrow(QueryParseException.class);
        mockMvc.perform(get(RoutesConfig.CLUBS_PATH + "?badquery:"))
                .andExpect(status().isBadRequest());
    }

}
