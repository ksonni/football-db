package com.ksonni.footballdb.players;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Position;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.players.dto.PatchPlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerResponse;
import com.ksonni.footballdb.players.dto.RegisterPlayerRequest;
import com.ksonni.footballdb.players.services.PlayersMapper;
import com.ksonni.footballdb.players.services.PlayersRepository;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.MathUtils;
import com.ksonni.footballdb.utils.MockMvcUtils;
import com.ksonni.footballdb.utils.MockUtils;
import com.ksonni.footballdb.utils.TestStringUtils;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@WebMvcTest(PlayersController.class)
class PlayersControllerTests {

    private static final String PLAYER_ID = "id";
    private static final String PLAYER_PATH = RoutesConfig.Players.PATH + "/" + PLAYER_ID;
    private static final int RANDOM_SQUAD_NUM = 10;
    private static final String LONG_COUNTRY_CODE = TestStringUtils.repeatedSequence("X", 5);

    private final MockMvcUtils utils = new MockMvcUtils();

    @MockBean
    private PlayersRepository playersRepository;
    @MockBean
    private QueryParser<Player> queryParser;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private PlayersMapper mapper;
    @MockBean
    private ClubsRepository clubsRepository;
    @MockBean
    private RateLimitingService rateLimitingService;
    @Autowired
    private MockMvc mockMvc;

    private List<Player> players;
    private Supplier<RegisterPlayerRequest.RegisterPlayerRequestBuilder> registerRequestSupplier;
    private RegisterPlayerRequest validRegisterRequest;
    private PatchPlayerRequest validPatchRequest;

    @BeforeEach
    void setup() {
        players = Arrays.asList(
                Player.builder().id("id").fullName("Some player").attackingWorkRate(WorkRate.HIGH)
                        .defensiveWorkRate(WorkRate.LOW).preferredFoot(Side.LEFT).build(),
                Player.builder().id("id2").fullName("Some player 2").build()
        );

        final Page<Player> pagedPlayers = new PageImpl<>(players,
                PageRequest.of(0, players.size()), players.size());
        BDDMockito.given(playersRepository.findAll(ArgumentMatchers.<Query<Player>>any()))
                .willReturn(pagedPlayers);

        for (Player player : players) {
            BDDMockito.given(mapper.toPlayerResponse(player)).willReturn(
                    PlayerResponse.builder().id(player.getId())
                            .fullName(player.getFullName())
                            .attackingWorkRate(player.getAttackingWorkRate())
                            .defensiveWorkRate(player.getDefensiveWorkRate())
                            .preferredFoot(player.getPreferredFoot())
                            .build()
            );
        }

        registerRequestSupplier = () -> RegisterPlayerRequest.builder()
                .fullName("Some player").preferredFoot(Side.LEFT).clubId("id")
                .squadNumber(RANDOM_SQUAD_NUM).position(Position.CENTER_FORWARD).countryCode("GB");
        validRegisterRequest = registerRequestSupplier.get().build();
        validPatchRequest = PatchPlayerRequest.builder().fullName("Some other player").build();
        MockUtils.disableRateLimiting(rateLimitingService);
    }

    @Test
    void enumeratePlayers() throws Exception {
        mockMvc.perform(utils.get(RoutesConfig.Players.PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(players.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", Is.is(players.get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].attackingWorkRate",
                        Is.is(players.get(0).getAttackingWorkRate().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].defensiveWorkRate",
                        Is.is(players.get(0).getDefensiveWorkRate().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].preferredFoot",
                        Is.is(players.get(0).getPreferredFoot().getValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id", Is.is(players.get(1).getId())));
    }

    @Test
    void enumeratePlayersInvalidQuery() throws Exception {
        BDDMockito.given(queryParser.parse(ArgumentMatchers.anyString()))
                .willThrow(QueryParseException.class);
        mockMvc.perform(utils.get(RoutesConfig.Players.PATH + "?badquery:"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    void registerPlayerEnforcesPermission() throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void registerPlayerValidatesData() throws Exception {
        final var base = registerRequestSupplier;
        final PlayerRequest[] badRequests = {
                RegisterPlayerRequest.builder().build(),
                base.get().fullName("").build(),
                base.get().fullName(TestStringUtils.longString()).build(),
                base.get().height(-1).build(),
                base.get().height(RegisterPlayerRequest.MAX_HEIGHT + 1).build(),
                base.get().weight(-1).build(),
                base.get().weight(RegisterPlayerRequest.MAX_WEIGHT + 1).build(),
                base.get().overall(-1).build(),
                base.get().overall(MathUtils.MAX_PERCENT + 1).build(),
                base.get().valueEuro(-1).build(),
                base.get().wageEuro(-1).build(),
                base.get().contractEndYear(-1).build(),
                base.get().contractStartYear(-1).build(),
                base.get().reputation(-1).build(),
                base.get().reputation(RegisterPlayerRequest.MAX_REPUTATION + 1).build(),
                base.get().shootingTotal(-1).build(),
                base.get().shootingTotal(MathUtils.MAX_PERCENT + 1).build(),
                base.get().passingTotal(-1).build(),
                base.get().passingTotal(MathUtils.MAX_PERCENT + 1).build(),
                base.get().dribblingTotal(-1).build(),
                base.get().dribblingTotal(MathUtils.MAX_PERCENT + 1).build(),
                base.get().defendingTotal(-1).build(),
                base.get().defendingTotal(MathUtils.MAX_PERCENT + 1).build(),
                base.get().headingAccuracy(-1).build(),
                base.get().headingAccuracy(MathUtils.MAX_PERCENT + 1).build(),
                base.get().penalties(-1).build(),
                base.get().penalties(MathUtils.MAX_PERCENT + 1).build(),
                base.get().squadNumber(-1).build(),
                base.get().birthYear(-1).build(),
                base.get().countryCode("").build(),
                base.get().countryCode(LONG_COUNTRY_CODE).build(),
        };

        for (var request : badRequests) {
            mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, request))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void registerPlayerDoesNotAcceptInvalidClubIds() throws Exception {
        final String clubId = validRegisterRequest.getClubId();
        BDDMockito.given(clubsRepository.findById(clubId)).willReturn(Optional.empty());

        mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void registerPlayerSucceeds() throws Exception {
        final String clubId = validRegisterRequest.getClubId();
        final Optional<Club> clubOptional = Optional.ofNullable(
                Club.builder().id(clubId).build());

        BDDMockito.given(clubsRepository.findById(clubId)).willReturn(clubOptional);

        BDDMockito.given(mapper.toPlayer(validRegisterRequest))
                .willReturn(Player.builder().id(PLAYER_ID).build());

        mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, validRegisterRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(playersRepository, Mockito.times(1))
                .save(ArgumentMatchers.any(Player.class));
    }

    @Test
    @WithMockUser
    void patchPlayerEnforcesPermission() throws Exception {
        mockMvc.perform(utils.patchJSON(PLAYER_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void patchPlayerValidatesData() throws Exception {
        final PatchPlayerRequest[] badRequests = {
                PatchPlayerRequest.builder().fullName("").build(),
                PatchPlayerRequest.builder().fullName(TestStringUtils.longString()).build(),
                PatchPlayerRequest.builder().height(-1).build(),
                PatchPlayerRequest.builder().height(PatchPlayerRequest.MAX_HEIGHT + 1).build(),
                PatchPlayerRequest.builder().weight(-1).build(),
                PatchPlayerRequest.builder().weight(PatchPlayerRequest.MAX_WEIGHT + 1).build(),
                PatchPlayerRequest.builder().overall(-1).build(),
                PatchPlayerRequest.builder().overall(MathUtils.MAX_PERCENT + 1).build(),
                PatchPlayerRequest.builder().valueEuro(-1).build(),
                PatchPlayerRequest.builder().wageEuro(-1).build(),
                PatchPlayerRequest.builder().contractEndYear(-1).build(),
                PatchPlayerRequest.builder().contractStartYear(-1).build(),
                PatchPlayerRequest.builder().reputation(-1).build(),
                PatchPlayerRequest.builder().reputation(PatchPlayerRequest.MAX_REPUTATION + 1).build(),
                PatchPlayerRequest.builder().shootingTotal(-1).build(),
                PatchPlayerRequest.builder().shootingTotal(MathUtils.MAX_PERCENT + 1).build(),
                PatchPlayerRequest.builder().passingTotal(-1).build(),
                PatchPlayerRequest.builder().passingTotal(MathUtils.MAX_PERCENT + 1).build(),
                PatchPlayerRequest.builder().dribblingTotal(-1).build(),
                PatchPlayerRequest.builder().dribblingTotal(MathUtils.MAX_PERCENT + 1).build(),
                PatchPlayerRequest.builder().defendingTotal(-1).build(),
                PatchPlayerRequest.builder().defendingTotal(MathUtils.MAX_PERCENT + 1).build(),
                PatchPlayerRequest.builder().headingAccuracy(-1).build(),
                PatchPlayerRequest.builder().headingAccuracy(MathUtils.MAX_PERCENT + 1).build(),
                PatchPlayerRequest.builder().penalties(-1).build(),
                PatchPlayerRequest.builder().penalties(MathUtils.MAX_PERCENT + 1).build(),
                PatchPlayerRequest.builder().squadNumber(-1).build(),
                PatchPlayerRequest.builder().birthYear(-1).build(),
                PatchPlayerRequest.builder().countryCode("").build(),
                PatchPlayerRequest.builder().countryCode(LONG_COUNTRY_CODE).build(),
        };

        for (var request : badRequests) {
            mockMvc.perform(utils.patchJSON(PLAYER_PATH, request))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void patchPlayerDoesNotAcceptInvalidClubIds() throws Exception {
        final String clubId = validRegisterRequest.getClubId();
        BDDMockito.given(clubsRepository.findById(clubId))
                .willReturn(Optional.empty());
        BDDMockito.given(playersRepository.findById(PLAYER_ID))
                .willReturn(Optional.ofNullable(Player.builder().build()));

        mockMvc.perform(utils.patchJSON(PLAYER_PATH,
                        PatchPlayerRequest.builder().clubId("bad id").build()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void patchPlayerRequestSucceeds() throws Exception {
        final Player player = Player.builder().id(PLAYER_ID).build();
        final Player updated = Player.builder().id(PLAYER_ID)
                .fullName(validPatchRequest.getFullName())
                .build();

        BDDMockito.given(playersRepository.findById(PLAYER_ID)).willReturn(Optional.ofNullable(player));
        BDDMockito.given(mapper.toPlayer(validPatchRequest, player)).willReturn(updated);

        mockMvc.perform(utils.patchJSON(PLAYER_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(playersRepository, Mockito.times(1)).save(updated);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void patchPlayerRejectsUnknownPlayers() throws Exception {
        BDDMockito.given(playersRepository.findById(PLAYER_ID)).willReturn(Optional.empty());
        mockMvc.perform(utils.patchJSON(PLAYER_PATH, validPatchRequest))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    void deletePlayerEnforcesPermission() throws Exception {
        mockMvc.perform(utils.delete(PLAYER_PATH))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void deletePlayerSucceeds() throws Exception {
        BDDMockito.given(playersRepository.findById(PLAYER_ID))
                .willReturn(Optional.ofNullable(Player.builder().build()));

        mockMvc.perform(utils.delete(PLAYER_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(playersRepository, Mockito.times(1)).deleteById(PLAYER_ID);
    }

    @Test
    @WithMockUser(roles = {Permission.Code.MANAGE_PLAYERS})
    void deletePlayerRejectsUnknownPlayers() throws Exception {
        BDDMockito.given(playersRepository.findById(PLAYER_ID)).willReturn(Optional.empty());
        mockMvc.perform(utils.delete(PLAYER_PATH))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void handlesRateLimitsReached() throws Exception {
        MockUtils.mockRateLimitReached(rateLimitingService);
        mockMvc.perform(utils.get(RoutesConfig.Players.PATH))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(playersRepository, queryParser, userDetailsService,
                clubsRepository, mapper, rateLimitingService);
    }

}
