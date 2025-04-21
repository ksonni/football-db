package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.dto.LeagueResponse;
import com.ksonni.footballdb.leagues.dto.PatchLeagueRequest;
import com.ksonni.footballdb.leagues.dto.RegisterLeagueRequest;
import com.ksonni.footballdb.leagues.services.LeaguesMapper;
import com.ksonni.footballdb.leagues.services.LeaguesRepository;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.MockMvcUtils;
import com.ksonni.footballdb.utils.TestStringUtils;
import com.ksonni.footballdb.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(LeaguesController.class)
class LeaguesControllerTests {

    private static final String LEAGUE_ID = "id";
    private static final String LEAGUES_PATH = RoutesConfig.Leagues.PATH + "/" + LEAGUE_ID;
    private final MockMvcUtils utils = new MockMvcUtils();
    @MockitoBean
    private LeaguesRepository leaguesRepository;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private LeaguesMapper mapper;
    @MockitoBean
    private RateLimitingService rateLimitingService;
    @Autowired
    private MockMvc mockMvc;
    private List<League> leagues;
    private RegisterLeagueRequest validRegisterRequest;
    private PatchLeagueRequest validPatchRequest;

    @BeforeEach
    void setup() {
        leagues = Arrays.asList(
                League.builder().id("id").name("Some league").build(),
                League.builder().id("id2").name("Some league 2").build()
        );

        for (League league : leagues) {
            BDDMockito.given(mapper.toLeagueResponse(league)).willReturn(
                    LeagueResponse.builder().id(league.getId()).name(league.getName()).build()
            );
        }

        validRegisterRequest = RegisterLeagueRequest.builder().name("League").build();
        validPatchRequest = PatchLeagueRequest.builder().name("Some other league").build();
        TestUtils.disableRateLimiting(rateLimitingService);
    }

    @Test
    @WithMockUser
    void registerLeagueEnforcesPermission() throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Leagues.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_LEAGUES})
    void registerLeagueValidatesData() throws Exception {
        final RegisterLeagueRequest[] badRequests = {
                RegisterLeagueRequest.builder().name("").build(),
                RegisterLeagueRequest.builder()
                        .name(TestStringUtils.longString()).build(),
        };

        for (var request : badRequests) {
            mockMvc.perform(utils.postJSON(RoutesConfig.Leagues.PATH, request))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_LEAGUES})
    void registerLeagueSucceeds() throws Exception {
        BDDMockito.given(mapper.toLeague(validRegisterRequest)).willReturn(
                League.builder().name(validRegisterRequest.getName()).build()
        );
        mockMvc.perform(utils.postJSON(RoutesConfig.Leagues.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(leaguesRepository, Mockito.times(1))
                .save(Mockito.any(League.class));
    }

    @Test
    @WithMockUser
    void patchLeagueEnforcesPermission() throws Exception {
        mockMvc.perform(utils.patchJSON(LEAGUES_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_LEAGUES})
    void patchLeagueValidatesData() throws Exception {
        final PatchLeagueRequest[] badRequests = {
                PatchLeagueRequest.builder()
                        .name(TestStringUtils.longString()).build(),
        };

        for (var request : badRequests) {
            mockMvc.perform(utils.patchJSON(LEAGUES_PATH, request))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_LEAGUES})
    void patchLeagueRequestSucceeds() throws Exception {
        final League league = League.builder().id(LEAGUE_ID).build();
        final League updated = League.builder().id(LEAGUE_ID)
                .name(validPatchRequest.getName()).build();

        BDDMockito.given(leaguesRepository.findById(LEAGUE_ID)).willReturn(Optional.ofNullable(league));
        BDDMockito.given(mapper.toLeague(validPatchRequest, league)).willReturn(updated);

        mockMvc.perform(utils.patchJSON(LEAGUES_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(leaguesRepository, Mockito.times(1)).save(updated);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_LEAGUES})
    void patchLeagueRejectsUnknownLeagues() throws Exception {
        BDDMockito.given(leaguesRepository.findById(LEAGUE_ID)).willReturn(Optional.empty());
        mockMvc.perform(utils.patchJSON(LEAGUES_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteLeagueEnforcesPermission() throws Exception {
        mockMvc.perform(utils.delete(LEAGUES_PATH))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DeletePermissions
    void deleteLeagueSucceeds() throws Exception {
        BDDMockito.given(leaguesRepository.findById(LEAGUE_ID))
                .willReturn(Optional.ofNullable(League.builder().build()));
        mockMvc.perform(utils.delete(LEAGUES_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(leaguesRepository, Mockito.times(1)).deleteById(LEAGUE_ID);
    }

    @Test
    @DeletePermissions
    void deleteLeagueRejectsUnknownLeagues() throws Exception {
        BDDMockito.given(leaguesRepository.findById(LEAGUE_ID))
                .willReturn(Optional.empty());
        mockMvc.perform(utils.delete(LEAGUES_PATH))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_LEAGUES})
    void handlesRateLimitsReached() throws Exception {
        TestUtils.mockRateLimitReached(rateLimitingService);
        mockMvc.perform(utils.delete(LEAGUES_PATH))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(leaguesRepository, userDetailsService, mapper, rateLimitingService);
    }

    @WithMockUser(roles = {
            Permission.Code.MANAGE_LEAGUES,
            Permission.Code.MANAGE_CLUBS,
            Permission.Code.MANAGE_PLAYERS
    })
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DeletePermissions {
    }

}
