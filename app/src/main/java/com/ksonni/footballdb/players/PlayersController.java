package com.ksonni.footballdb.players;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.dto.PatchPlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerResponse;
import com.ksonni.footballdb.players.dto.RegisterPlayerRequest;
import com.ksonni.footballdb.players.services.PlayersMapper;
import com.ksonni.footballdb.players.services.PlayersRepository;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.DocUtils;
import com.ksonni.footballdb.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.Players.PATH)
@Tag(name = "Players", description = "REST endpoints to manage players")
public class PlayersController {

    private final PlayersRepository playersRepository;
    private final QueryParser<Player> queryParser;
    private final PlayersMapper playersMapper;

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
    @Operation(
        summary = "Register a new player",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
    )
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
    @Operation(
        summary = "Update an existing player",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
    )
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
    @Operation(
        summary = "Delete an existing player",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_PLAYERS
    )
    public void deletePlayer(final @PathVariable("id") String id) {
        playersRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        playersRepository.deleteById(id);
        log.info("deleted player {}", id);
    }

}
