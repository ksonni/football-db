package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.dto.LeagueResponse;
import com.ksonni.footballdb.leagues.dto.PatchLeagueRequest;
import com.ksonni.footballdb.leagues.dto.RegisterLeagueRequest;
import com.ksonni.footballdb.leagues.services.LeaguesMapper;
import com.ksonni.footballdb.leagues.services.LeaguesRepository;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.MockMvcUtils;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ksonni.footballdb.utils.TestStringUtils.longString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaguesController.class)
class LeaguesControllerTests {

    @MockBean
    LeaguesRepository leaguesRepository;
    @MockBean
    QueryParser<League> queryParser;
    @MockBean
    UserDetailsService userDetailsService;
    @MockBean
    LeaguesMapper mapper;

    @Autowired
    MockMvc mockMvc;

    List<League> leagues;
    RegisterLeagueRequest validRegisterRequest;
    PatchLeagueRequest validPatchRequest;

    private static final String LEAGUE_ID = "id";
    private final String LEAGUES_PATH = RoutesConfig.Leagues.PATH + "/" + LEAGUE_ID;

    private final MockMvcUtils utils = new MockMvcUtils();

    @WithMockUser(roles = {
        Permission.Code.MANAGE_LEAGUES,
        Permission.Code.MANAGE_CLUBS,
        Permission.Code.MANAGE_PLAYERS
    })
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DeletePermissions {}

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

        for (League league: leagues) {
            given(mapper.toLeagueResponse(league)).willReturn(
                LeagueResponse.builder().id(league.getId()).name(league.getName()).build()
            );
        }

        validRegisterRequest = RegisterLeagueRequest.builder().name("League").build();
        validPatchRequest = PatchLeagueRequest.builder().name("Some other league").build();
    }

    @AfterEach
    void tearDown() {
        reset(leaguesRepository, queryParser, userDetailsService, mapper);
    }

    @Test
    void enumerateLeagues() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Leagues.PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(leagues.size())))
                .andExpect(jsonPath("$.content[0].id", is(leagues.get(0).getId())))
                .andExpect(jsonPath("$.content[1].id", is(leagues.get(1).getId())));
    }

    @Test
    void enumerateLeaguesInvalidQuery() throws Exception {
        given(queryParser.parse(anyString())).willThrow(QueryParseException.class);
        mockMvc.perform(utils.get(RoutesConfig.Leagues.PATH + "?badquery:"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void registerLeagueEnforcesPermission() throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Leagues.PATH, validRegisterRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_LEAGUES })
    void registerLeagueValidatesData() throws Exception {
        RegisterLeagueRequest[] badRequests = {
            RegisterLeagueRequest.builder().name("").build(),
            RegisterLeagueRequest.builder().name(longString()).build(),
        };

        for (var request: badRequests) {
            mockMvc.perform(utils.postJSON(RoutesConfig.Leagues.PATH, request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_LEAGUES })
    void registerLeagueSucceeds() throws Exception {
        given(mapper.toLeague(validRegisterRequest)).willReturn(
            League.builder().name(validRegisterRequest.getName()).build()
        );
        mockMvc.perform(utils.postJSON(RoutesConfig.Leagues.PATH, validRegisterRequest))
                .andExpect(status().isCreated());

        verify(leaguesRepository, times(1)).save(any(League.class));
    }

    @Test
    @WithMockUser
    void patchLeagueEnforcesPermission() throws Exception {
        mockMvc.perform(utils.patchJSON(LEAGUES_PATH, validPatchRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_LEAGUES })
    void patchLeagueValidatesData() throws Exception {
        PatchLeagueRequest[] badRequests = {
            PatchLeagueRequest.builder().name(longString()).build(),
        };

        for (var request: badRequests) {
            mockMvc.perform(utils.patchJSON(LEAGUES_PATH, request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_LEAGUES })
    void patchLeagueRequestSucceeds() throws Exception {
        League league = League.builder().id(LEAGUE_ID).build();
        League updated = League.builder().id(LEAGUE_ID)
                .name(validPatchRequest.getName()).build();

        given(leaguesRepository.findById(LEAGUE_ID)).willReturn(Optional.ofNullable(league));
        given(mapper.toLeague(validPatchRequest, league)).willReturn(updated);

        mockMvc.perform(utils.patchJSON(LEAGUES_PATH, validPatchRequest))
                .andExpect(status().isOk());

        verify(leaguesRepository, times(1)).save(updated);
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_LEAGUES })
    void patchLeagueRejectsUnknownLeagues() throws Exception {
        given(leaguesRepository.findById(LEAGUE_ID)).willReturn(Optional.empty());
        mockMvc.perform(utils.patchJSON(LEAGUES_PATH, validPatchRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteLeagueEnforcesPermission() throws Exception {
        mockMvc.perform(utils.delete(LEAGUES_PATH)).andExpect(status().isForbidden());
    }

    @Test
    @DeletePermissions
    void deleteLeagueSucceeds() throws Exception {
        given(leaguesRepository.findById(LEAGUE_ID))
                .willReturn(Optional.ofNullable(League.builder().build()));
        mockMvc.perform(utils.delete(LEAGUES_PATH)).andExpect(status().isOk());
        verify(leaguesRepository, times(1)).deleteById(LEAGUE_ID);
    }

    @Test
    @DeletePermissions
    void deleteLeagueRejectsUnknownLeagues() throws Exception {
        given(leaguesRepository.findById(LEAGUE_ID))
                .willReturn(Optional.empty());
        mockMvc.perform(utils.delete(LEAGUES_PATH)).andExpect(status().isNotFound());
    }

}
