package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.dto.ClubResponse;
import com.ksonni.footballdb.clubs.dto.PatchClubRequest;
import com.ksonni.footballdb.clubs.dto.RegisterClubRequest;
import com.ksonni.footballdb.clubs.services.ClubsMapper;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.leagues.domain.League;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClubsController.class)
class ClubsControllerTests {

    @MockBean
    ClubsRepository clubsRepository;
    @MockBean
    ClubsMapper mapper;
    @MockBean
    LeaguesRepository leaguesRepository;
    @MockBean
    QueryParser<Club> queryParser;
    @MockBean
    UserDetailsService userDetailsService;

    @Autowired
    MockMvc mockMvc;

    List<Club> clubs;
    RegisterClubRequest validRegisterRequest;
    PatchClubRequest validPatchRequest;

    private static final String CLUB_ID = "id";
    private static final String CLUB_PATH = RoutesConfig.Clubs.PATH + "/" + CLUB_ID;

    private final MockMvcUtils utils = new MockMvcUtils();

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
        for (Club club: clubs) {
            given(mapper.toClubResponse(club)).willReturn(
                ClubResponse.builder().id(club.getId()).name(club.getName()).build()
            );
        }

        validRegisterRequest = RegisterClubRequest.builder().name("Some club")
                .leagueId("LeagueId")
                .build();
        validPatchRequest = PatchClubRequest.builder().name("Some other club").build();
    }

    @AfterEach
    void tearDown() {
        reset(clubsRepository, queryParser, userDetailsService, leaguesRepository, mapper);
    }

    @Test
    void enumerateClubs() throws Exception {
        mockMvc.perform(get(RoutesConfig.Clubs.PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(clubs.size())))
                .andExpect(jsonPath("$.content[0].id", is(clubs.get(0).getId())))
                .andExpect(jsonPath("$.content[1].id", is(clubs.get(1).getId())));
    }

    @Test
    void enumerateClubsInvalidQuery() throws Exception {
        given(queryParser.parse(anyString())).willThrow(QueryParseException.class);
        mockMvc.perform(get(RoutesConfig.Clubs.PATH + "?badquery:"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void registerClubEnforcesPermission() throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, validRegisterRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_CLUBS })
    void registerClubValidatesData() throws Exception {
        Supplier<RegisterClubRequest.RegisterClubRequestBuilder<?, ?>> base =
                () -> RegisterClubRequest.builder()
                .name("Test").leagueId("League ID");

        RegisterClubRequest badRequests[] = {
            RegisterClubRequest.builder().build(),
            base.get().overallRating(-1).build(),
            base.get().overallRating(101).build(),
            base.get().attackRating(-1).build(),
            base.get().attackRating(101).build(),
            base.get().midfieldRating(-1).build(),
            base.get().midfieldRating(101).build(),
            base.get().defenseRating(-1).build(),
            base.get().defenseRating(101).build(),
            base.get().transferBudget(-1).build(),
            base.get().domesticPrestige(-1).build(),
            base.get().domesticPrestige(11).build(),
            base.get().internationalPrestige(-1).build(),
            base.get().internationalPrestige(11).build()
        };

        for (var request: badRequests) {
            mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_CLUBS })
    void registerClubDoesNotAcceptInvalidLeagueIds() throws Exception {
        String leagueId = validRegisterRequest.getLeagueId();
        given(leaguesRepository.findById(leagueId))
                .willReturn(Optional.ofNullable(null));

        mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, validRegisterRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_CLUBS })
    void registerClubSucceeds() throws Exception {
        String leagueId = validRegisterRequest.getLeagueId();
        Optional<League> leagueOptional = Optional.ofNullable(
                League.builder().id(leagueId).build());

        given(leaguesRepository.findById(leagueId)).willReturn(leagueOptional);

        given(mapper.toClub(validRegisterRequest))
            .willReturn(Club.builder().id(CLUB_ID)
                .name(validPatchRequest.getName())
                .leagueId(leagueId).build());

        mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, validRegisterRequest))
                .andExpect(status().isCreated());

        verify(clubsRepository, times(1)).save(any(Club.class));
    }

    @Test
    @WithMockUser
    void patchClubEnforcesPermission() throws Exception {
        mockMvc.perform(utils.patchJSON(CLUB_PATH, validPatchRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_CLUBS })
    void patchClubValidatesData() throws Exception {
        PatchClubRequest badRequests[] = {
            PatchClubRequest.builder().overallRating(-1).build(),
            PatchClubRequest.builder().overallRating(101).build(),
            PatchClubRequest.builder().attackRating(-1).build(),
            PatchClubRequest.builder().attackRating(101).build(),
            PatchClubRequest.builder().midfieldRating(-1).build(),
            PatchClubRequest.builder().midfieldRating(101).build(),
            PatchClubRequest.builder().defenseRating(-1).build(),
            PatchClubRequest.builder().defenseRating(101).build(),
            PatchClubRequest.builder().transferBudget(-1).build(),
            PatchClubRequest.builder().domesticPrestige(-1).build(),
            PatchClubRequest.builder().domesticPrestige(11).build(),
            PatchClubRequest.builder().internationalPrestige(-1).build(),
            PatchClubRequest.builder().internationalPrestige(11).build()
        };

        for (var request: badRequests) {
            mockMvc.perform(utils.patchJSON(CLUB_PATH, request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_CLUBS })
    void patchClubDoesNotAcceptInvalidLeagueIds() throws Exception {
        String leagueId = validRegisterRequest.getLeagueId();
        given(leaguesRepository.findById(leagueId))
                .willReturn(Optional.ofNullable(null));
        given(clubsRepository.findById(CLUB_ID))
                .willReturn(Optional.ofNullable(Club.builder().build()));

        mockMvc.perform(utils.patchJSON(CLUB_PATH,
                        PatchClubRequest.builder().leagueId("bad id").build()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteClubEnforcesPermission() throws Exception {
        mockMvc.perform(delete(CLUB_PATH)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_CLUBS })
    void deleteClubSucceeds() throws Exception {
        given(clubsRepository.findById(CLUB_ID))
                .willReturn(Optional.ofNullable(Club.builder().build()));

        mockMvc.perform(delete(CLUB_PATH)).andExpect(status().isOk());
    }

}
