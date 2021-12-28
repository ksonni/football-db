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
import com.ksonni.footballdb.utils.MathUtils;
import com.ksonni.footballdb.utils.MockMvcUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


@WebMvcTest(ClubsController.class)
class ClubsControllerTests {

    private static final String CLUB_ID = "id";
    private static final String CLUB_PATH = RoutesConfig.Clubs.PATH + "/" + CLUB_ID;
    private final MockMvcUtils utils = new MockMvcUtils();

    @MockBean
    private ClubsRepository clubsRepository;
    @MockBean
    private ClubsMapper mapper;
    @MockBean
    private LeaguesRepository leaguesRepository;
    @MockBean
    private QueryParser<Club> queryParser;
    @MockBean
    private UserDetailsService userDetailsService;
    @Autowired
    private MockMvc mockMvc;
    private List<Club> clubs;
    private RegisterClubRequest validRegisterRequest;
    private PatchClubRequest validPatchRequest;

    @BeforeEach
    void setup() {
        clubs = Arrays.asList(
                Club.builder().id("id").name("Some club").build(),
                Club.builder().id("id2").name("Some club 2").build()
        );

        final Page<Club> pagedClubs = new PageImpl<>(clubs,
                PageRequest.of(0, 2), 2);
        BDDMockito.given(clubsRepository.findAll(ArgumentMatchers.<Query<Club>>any()))
                .willReturn(pagedClubs);

        for (Club club : clubs) {
            BDDMockito.given(mapper.toClubResponse(club)).willReturn(
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
        Mockito.reset(clubsRepository, queryParser, userDetailsService, leaguesRepository, mapper);
    }

    @Test
    void enumerateClubs() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Clubs.PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(clubs.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", Is.is(clubs.get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id", Is.is(clubs.get(1).getId())));
    }

    @Test
    void enumerateClubsInvalidQuery() throws Exception {
        BDDMockito.given(queryParser.parse(ArgumentMatchers.anyString()))
                .willThrow(QueryParseException.class);
        mockMvc.perform(utils.get(RoutesConfig.Clubs.PATH + "?badquery:"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    void registerClubEnforcesPermission() throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_CLUBS})
    void registerClubValidatesData() throws Exception {
        final Supplier<RegisterClubRequest.RegisterClubRequestBuilder<?, ?>> base =
                () -> RegisterClubRequest.builder()
                        .name("Test").leagueId("League ID");

        final RegisterClubRequest[] badRequests = {
                RegisterClubRequest.builder().build(),
                base.get().overallRating(-1).build(),
                base.get().overallRating(MathUtils.MAX_PERCENT + 1).build(),
                base.get().attackRating(-1).build(),
                base.get().attackRating(MathUtils.MAX_PERCENT + 1).build(),
                base.get().midfieldRating(-1).build(),
                base.get().midfieldRating(MathUtils.MAX_PERCENT + 1).build(),
                base.get().defenseRating(-1).build(),
                base.get().defenseRating(MathUtils.MAX_PERCENT + 1).build(),
                base.get().transferBudget(-1).build(),
                base.get().domesticPrestige(-1).build(),
                base.get().domesticPrestige(RegisterClubRequest.MAX_PRESTIGE + 1).build(),
                base.get().internationalPrestige(-1).build(),
                base.get().internationalPrestige(RegisterClubRequest.MAX_PRESTIGE + 1).build(),
        };

        for (var request : badRequests) {
            mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, request))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_CLUBS})
    void registerClubDoesNotAcceptInvalidLeagueIds() throws Exception {
        final String leagueId = validRegisterRequest.getLeagueId();
        BDDMockito.given(leaguesRepository.findById(leagueId))
                .willReturn(Optional.empty());

        mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_CLUBS})
    void registerClubSucceeds() throws Exception {
        final String leagueId = validRegisterRequest.getLeagueId();
        final Optional<League> leagueOptional = Optional.ofNullable(
                League.builder().id(leagueId).build());

        BDDMockito.given(leaguesRepository.findById(leagueId)).willReturn(leagueOptional);

        BDDMockito.given(mapper.toClub(validRegisterRequest))
                .willReturn(Club.builder().id(CLUB_ID)
                        .name(validPatchRequest.getName())
                        .leagueId(leagueId).build());

        mockMvc.perform(utils.postJSON(RoutesConfig.Clubs.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(clubsRepository, Mockito.times(1)).save(Mockito.any(Club.class));
    }

    @Test
    @WithMockUser
    void patchClubEnforcesPermission() throws Exception {
        mockMvc.perform(utils.patchJSON(CLUB_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_CLUBS})
    void patchClubValidatesData() throws Exception {
        final PatchClubRequest[] badRequests = {
                PatchClubRequest.builder().overallRating(-1).build(),
                PatchClubRequest.builder().overallRating(MathUtils.MAX_PERCENT + 1).build(),
                PatchClubRequest.builder().attackRating(-1).build(),
                PatchClubRequest.builder().attackRating(MathUtils.MAX_PERCENT + 1).build(),
                PatchClubRequest.builder().midfieldRating(-1).build(),
                PatchClubRequest.builder().midfieldRating(MathUtils.MAX_PERCENT + 1).build(),
                PatchClubRequest.builder().defenseRating(-1).build(),
                PatchClubRequest.builder().defenseRating(MathUtils.MAX_PERCENT + 1).build(),
                PatchClubRequest.builder().transferBudget(-1).build(),
                PatchClubRequest.builder().domesticPrestige(-1).build(),
                PatchClubRequest.builder().domesticPrestige(PatchClubRequest.MAX_PRESTIGE + 1).build(),
                PatchClubRequest.builder().internationalPrestige(-1).build(),
                PatchClubRequest.builder().internationalPrestige(PatchClubRequest.MAX_PRESTIGE + 1).build(),
        };

        for (var request : badRequests) {
            mockMvc.perform(utils.patchJSON(CLUB_PATH, request))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_CLUBS})
    void patchClubDoesNotAcceptInvalidLeagueIds() throws Exception {
        final String leagueId = validRegisterRequest.getLeagueId();
        BDDMockito.given(leaguesRepository.findById(leagueId))
                .willReturn(Optional.empty());
        BDDMockito.given(clubsRepository.findById(CLUB_ID))
                .willReturn(Optional.ofNullable(Club.builder().build()));

        mockMvc.perform(utils.patchJSON(CLUB_PATH,
                        PatchClubRequest.builder().leagueId("bad id").build()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_CLUBS})
    void patchClubRequestSucceeds() throws Exception {
        final Club club = Club.builder().id(CLUB_ID).build();
        final Club updated = Club.builder().id(CLUB_ID)
                .name(validPatchRequest.getName()).build();

        BDDMockito.given(clubsRepository.findById(CLUB_ID)).willReturn(Optional.ofNullable(club));
        BDDMockito.given(mapper.toClub(validPatchRequest, club)).willReturn(updated);

        mockMvc.perform(utils.patchJSON(CLUB_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(clubsRepository, Mockito.times(1)).save(updated);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_CLUBS})
    void patchClubRejectsUnknownClubs() throws Exception {
        BDDMockito.given(clubsRepository.findById(CLUB_ID)).willReturn(Optional.empty());
        mockMvc.perform(utils.patchJSON(CLUB_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteClubEnforcesPermission() throws Exception {
        mockMvc.perform(utils.delete(CLUB_PATH))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DeletePermissions
    void deleteClubSucceeds() throws Exception {
        BDDMockito.given(clubsRepository.findById(CLUB_ID))
                .willReturn(Optional.ofNullable(Club.builder().build()));

        mockMvc.perform(utils.delete(CLUB_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(clubsRepository, Mockito.times(1)).deleteById(CLUB_ID);
    }

    @Test
    @DeletePermissions
    void deleteClubRejectsUnknownClubs() throws Exception {
        BDDMockito.given(clubsRepository.findById(CLUB_ID)).willReturn(Optional.empty());
        mockMvc.perform(utils.delete(CLUB_PATH))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(roles = {
            Permission.Code.MANAGE_CLUBS,
            Permission.Code.MANAGE_PLAYERS
    })
    @Retention(RetentionPolicy.RUNTIME)
    private @interface DeletePermissions {
    }


}
