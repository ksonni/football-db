package com.ksonni.footballdb.players;

import com.ksonni.footballdb.config.RoutesConfig;
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
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.Players.PATH)
@PlayersControllerDoc
public class PlayersController {

    private final PlayersRepository playersRepository;
    private final QueryParser<Player> queryParser;
    private final PlayersMapper playersMapper;

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
                .map(playersMapper::toPlayerResponse);
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
        final Player player = playersMapper.toPlayer(request);
        player.setId(StringUtils.uuid());
        playersRepository.save(player);
        log.info("created player {}", player.getId());
        return playersMapper.toPlayerResponse(player);
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
        final Player currentPlayer = playersRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        final Player player = playersMapper.toPlayer(request, currentPlayer);
        playersRepository.save(player);
        log.info("updated player {}", player.getId());
        return playersMapper.toPlayerResponse(player);
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
        playersRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        playersRepository.deleteById(id);
        log.info("deleted player {}", id);
    }

}
