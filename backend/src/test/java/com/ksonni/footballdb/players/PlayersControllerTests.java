package com.ksonni.footballdb.players;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.players.dto.PatchPlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerResponse;
import com.ksonni.footballdb.players.dto.RegisterPlayerRequest;
import com.ksonni.footballdb.players.services.PlayersMapper;
import com.ksonni.footballdb.players.services.PlayersRepository;
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

import static com.ksonni.footballdb.utils.TestStringUtils.longString;
import static com.ksonni.footballdb.utils.TestStringUtils.repeatedSequence;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayersController.class)
class PlayersControllerTests {

    @MockBean
    PlayersRepository playersRepository;
    @MockBean
    QueryParser<Player> queryParser;
    @MockBean
    UserDetailsService userDetailsService;
    @MockBean
    PlayersMapper mapper;
    @MockBean
    ClubsRepository clubsRepository;

    @Autowired
    MockMvc mockMvc;

    List<Player> players;
    Supplier<RegisterPlayerRequest.RegisterPlayerRequestBuilder> registerRequestSupplier;
    RegisterPlayerRequest validRegisterRequest;
    PatchPlayerRequest validPatchRequest;

    private static final String PLAYER_ID = "id";
    private static final String PLAYER_PATH = RoutesConfig.Players.PATH + "/" + PLAYER_ID;

    private final MockMvcUtils utils = new MockMvcUtils();

    @BeforeEach
    void setup() {
        players = Arrays.asList(
            Player.builder().id("id").fullName("Some player").attackingWorkRate(WorkRate.HIGH)
                    .defensiveWorkRate(WorkRate.LOW).preferredFoot(Side.LEFT).build(),
            Player.builder().id("id2").fullName("Some player 2").build()
        );

        Page<Player> pagedPlayers = new PageImpl<>(players,
                PageRequest.of(0, players.size()), players.size());
        given(playersRepository.findAll(ArgumentMatchers.<Query<Player>>any()))
                .willReturn(pagedPlayers);

        for (Player player: players) {
            given(mapper.toPlayerResponse(player)).willReturn(
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
                    .squadNumber(10).position("FW").countryCode("GB");
        validRegisterRequest = registerRequestSupplier.get().build();
        validPatchRequest = PatchPlayerRequest.builder().fullName("Some other player").build();
    }

    @AfterEach
    void tearDown() {
        reset(playersRepository, queryParser, userDetailsService,
                clubsRepository, mapper);
    }

    @Test
    void enumeratePlayers() throws Exception {
        mockMvc.perform(get(RoutesConfig.Players.PATH))
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
        mockMvc.perform(get(RoutesConfig.Players.PATH + "?badquery:"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void registerPlayerEnforcesPermission() throws Exception {
        mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, validRegisterRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void registerPlayerValidatesData() throws Exception {
        var base = registerRequestSupplier;
        RegisterPlayerRequest[] badRequests = {
            RegisterPlayerRequest.builder().build(),
            base.get().fullName("").build(),
            base.get().fullName(longString()).build(),
            base.get().height(-1).build(),
            base.get().height(501).build(),
            base.get().weight(-1).build(),
            base.get().weight(1001).build(),
            base.get().overall(-1).build(),
            base.get().overall(101).build(),
            base.get().valueEuro(-1).build(),
            base.get().wageEuro(-1).build(),
            base.get().contractEndYear(-1).build(),
            base.get().contractStartYear(-1).build(),
            base.get().reputation(-1).build(),
            base.get().reputation(11).build(),
            base.get().shootingTotal(-1).build(),
            base.get().shootingTotal(101).build(),
            base.get().passingTotal(-1).build(),
            base.get().passingTotal(101).build(),
            base.get().dribblingTotal(-1).build(),
            base.get().dribblingTotal(101).build(),
            base.get().defendingTotal(-1).build(),
            base.get().defendingTotal(101).build(),
            base.get().headingAccuracy(-1).build(),
            base.get().headingAccuracy(101).build(),
            base.get().penalties(-1).build(),
            base.get().penalties(101).build(),
            base.get().squadNumber(-1).build(),
            base.get().birthYear(-1).build(),
            base.get().countryCode("").build(),
            base.get().countryCode(repeatedSequence("X", 5)).build(),
        };

        for (var request: badRequests) {
            mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void registerPlayerDoesNotAcceptInvalidClubIds() throws Exception {
        String clubId = validRegisterRequest.getClubId();
        given(clubsRepository.findById(clubId)).willReturn(Optional.empty());

        mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, validRegisterRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void registerPlayerSucceeds() throws Exception {
        String clubId = validRegisterRequest.getClubId();
        Optional<Club> clubOptional = Optional.ofNullable(
            Club.builder().id(clubId).build());

        given(clubsRepository.findById(clubId)).willReturn(clubOptional);

        given(mapper.toPlayer(validRegisterRequest))
                .willReturn(Player.builder().id(PLAYER_ID).build());

        mockMvc.perform(utils.postJSON(RoutesConfig.Players.PATH, validRegisterRequest))
                .andExpect(status().isCreated());

        verify(playersRepository, times(1)).save(any(Player.class));
    }

    @Test
    @WithMockUser
    void patchPlayerEnforcesPermission() throws Exception {
        mockMvc.perform(utils.patchJSON(PLAYER_PATH, validPatchRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void patchPlayerValidatesData() throws Exception {
        PatchPlayerRequest[] badRequests = {
            PatchPlayerRequest.builder().fullName("").build(),
            PatchPlayerRequest.builder().fullName(longString()).build(),
            PatchPlayerRequest.builder().height(-1).build(),
            PatchPlayerRequest.builder().height(501).build(),
            PatchPlayerRequest.builder().weight(-1).build(),
            PatchPlayerRequest.builder().weight(1001).build(),
            PatchPlayerRequest.builder().overall(-1).build(),
            PatchPlayerRequest.builder().overall(101).build(),
            PatchPlayerRequest.builder().valueEuro(-1).build(),
            PatchPlayerRequest.builder().wageEuro(-1).build(),
            PatchPlayerRequest.builder().contractEndYear(-1).build(),
            PatchPlayerRequest.builder().contractStartYear(-1).build(),
            PatchPlayerRequest.builder().reputation(-1).build(),
            PatchPlayerRequest.builder().reputation(11).build(),
            PatchPlayerRequest.builder().shootingTotal(-1).build(),
            PatchPlayerRequest.builder().shootingTotal(101).build(),
            PatchPlayerRequest.builder().passingTotal(-1).build(),
            PatchPlayerRequest.builder().passingTotal(101).build(),
            PatchPlayerRequest.builder().dribblingTotal(-1).build(),
            PatchPlayerRequest.builder().dribblingTotal(101).build(),
            PatchPlayerRequest.builder().defendingTotal(-1).build(),
            PatchPlayerRequest.builder().defendingTotal(101).build(),
            PatchPlayerRequest.builder().headingAccuracy(-1).build(),
            PatchPlayerRequest.builder().headingAccuracy(101).build(),
            PatchPlayerRequest.builder().penalties(-1).build(),
            PatchPlayerRequest.builder().penalties(101).build(),
            PatchPlayerRequest.builder().squadNumber(-1).build(),
            PatchPlayerRequest.builder().birthYear(-1).build(),
            PatchPlayerRequest.builder().countryCode("").build(),
            PatchPlayerRequest.builder().countryCode(repeatedSequence("X", 5)).build(),
        };

        for (var request: badRequests) {
            mockMvc.perform(utils.patchJSON(PLAYER_PATH, request))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void patchPlayerDoesNotAcceptInvalidClubIds() throws Exception {
        String clubId = validRegisterRequest.getClubId();
        given(clubsRepository.findById(clubId))
            .willReturn(Optional.empty());
        given(playersRepository.findById(PLAYER_ID))
                .willReturn(Optional.ofNullable(Player.builder().build()));

        mockMvc.perform(utils.patchJSON(PLAYER_PATH,
                PatchPlayerRequest.builder().clubId("bad id").build()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void patchPlayerRequestSucceeds() throws Exception {
        Player player = Player.builder().id(PLAYER_ID).build();
        Player updated = Player.builder().id(PLAYER_ID)
                .fullName(validPatchRequest.getFullName())
                .build();

        given(playersRepository.findById(PLAYER_ID)).willReturn(Optional.ofNullable(player));
        given(mapper.toPlayer(validPatchRequest, player)).willReturn(updated);

        mockMvc.perform(utils.patchJSON(PLAYER_PATH, validPatchRequest))
                .andExpect(status().isOk());

        verify(playersRepository, times(1)).save(updated);
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void patchPlayerRejectsUnknownPlayers() throws Exception {
        given(playersRepository.findById(PLAYER_ID)).willReturn(Optional.empty());
        mockMvc.perform(utils.patchJSON(PLAYER_PATH, validPatchRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deletePlayerEnforcesPermission() throws Exception {
        mockMvc.perform(delete(PLAYER_PATH)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void deletePlayerSucceeds() throws Exception {
        given(playersRepository.findById(PLAYER_ID))
            .willReturn(Optional.ofNullable(Player.builder().build()));

        mockMvc.perform(delete(PLAYER_PATH)).andExpect(status().isOk());
        verify(playersRepository, times(1)).deleteById(PLAYER_ID);
    }

    @Test
    @WithMockUser(roles = { Permission.Code.MANAGE_PLAYERS })
    void deletePlayerRejectsUnknownPlayers() throws Exception {
        given(playersRepository.findById(PLAYER_ID)).willReturn(Optional.empty());
        mockMvc.perform(delete(PLAYER_PATH)).andExpect(status().isNotFound());
    }

}
