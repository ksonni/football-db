package com.ksonni.footballdb.players;

import com.ksonni.footballdb.clubs.services.ClubsRepository;
import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.files.services.FilesRepository;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.dto.PatchPlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerResponse;
import com.ksonni.footballdb.players.dto.RegisterPlayerRequest;
import com.ksonni.footballdb.players.services.PlayersMapper;
import com.ksonni.footballdb.players.services.PlayersRepository;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.Players.PATH)
@PlayersControllerDoc
public class PlayersController {

    private final ClubsRepository clubsRepository;
    private final FilesRepository filesRepository;
    private final PlayersRepository playersRepository;
    private final QueryParser<Player> queryParser;
    private final PlayersMapper mapper;

    /**
     * Query players.
     *
     * @param request HTTP request
     * @return Paginated list of players
     * @throws QueryParseException if the query is not valid
     */
    @GetMapping
    @Transactional(readOnly = true)
    @EnumeratePlayersDoc
    public Page<PlayerResponse> enumeratePlayers(final HttpServletRequest request) throws QueryParseException {
        final String query = request.getQueryString();
        log.info("Processing query: {}", query);
        return playersRepository.findAll(queryParser.parse(query))
                .map(mapper::toPlayerResponse);
    }

    /**
     * Register a new player.
     *
     * @param request player registration request
     * @return the created player
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({Permission.Code.MANAGE_PLAYERS})
    @Transactional
    @RegisterPlayerDoc
    public PlayerResponse registerPlayer(final @Valid @RequestBody RegisterPlayerRequest request) {
        clubsRepository.findById(request.getClubId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid club ID"));

        if (request.getImage() != null) {
            filesRepository.findById(request.getImage()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file ID for image field"));
        }

        final Player player = mapper.toPlayer(request);
        player.setId(StringUtils.uuid());
        playersRepository.save(player);
        log.info("created player {}", player.getId());
        return mapper.toPlayerResponse(player);
    }

    /**
     * Partially update a player.
     *
     * @param id      id of the player to update
     * @param request player update request
     * @return success/error response
     */
    @PatchMapping("/{id}")
    @RolesAllowed({Permission.Code.MANAGE_PLAYERS})
    @Transactional
    @PatchPlayerDoc
    public PlayerResponse patchPlayer(final @PathVariable("id") String id,
                                      final @Valid @RequestBody PatchPlayerRequest request) {
        final Optional<Player> playerOptional = playersRepository.findById(id);
        if (playerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }

        if (request.getClubId() != null
                && clubsRepository.findById(request.getClubId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Club ID");
        }
        if (request.getImage() != null) {
            filesRepository.findById(request.getImage()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file ID for image field"));
        }

        final Player player = mapper.toPlayer(request, playerOptional.get());
        playersRepository.save(player);
        log.info("updated player {}", player.getId());
        return mapper.toPlayerResponse(player);
    }

    /**
     * Delete a player.
     *
     * @param id id of the player to delete.
     */
    @DeleteMapping("/{id}")
    @RolesAllowed({Permission.Code.MANAGE_PLAYERS})
    @Transactional
    @DeletePlayerDoc
    public void deleteClub(final @PathVariable("id") String id) {
        final Optional<Player> playerOptional = playersRepository.findById(id);
        if (playerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        playersRepository.deleteById(id);
        log.info("deleted player {}", id);
    }

}
