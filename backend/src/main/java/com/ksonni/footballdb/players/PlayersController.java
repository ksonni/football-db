package com.ksonni.footballdb.players;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
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
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.Players.PATH)
public class PlayersController {

    private final ClubsRepository clubsRepository;
    private final PlayersRepository playersRepository;
    private final QueryParser<Player> queryParser;
    private final PlayersMapper mapper;

    @GetMapping
    @Transactional(readOnly = true)
    public Page<PlayerResponse> enumeratePlayers(HttpServletRequest request) throws QueryParseException {
        return playersRepository.findAll(queryParser.parse(request.getQueryString()))
                .map(mapper::toPlayerResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({ Permission.Code.MANAGE_PLAYERS })
    @Transactional
    public PlayerResponse registerPlayer(@Valid @RequestBody RegisterPlayerRequest request) {
        Optional<Club> club = clubsRepository.findById(request.getClubId());
        if (club.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid club ID");
        }

        Player player = mapper.toPlayer(request);
        player.setId(StringUtils.uuid());
        playersRepository.save(player);
        return mapper.toPlayerResponse(player);
    }

    @PatchMapping("/{id}")
    @RolesAllowed({ Permission.Code.MANAGE_PLAYERS })
    @Transactional
    public PlayerResponse patchPlayer(@PathVariable("id") String id, @Valid @RequestBody PatchPlayerRequest request) {
        Optional<Player> playerOptional = playersRepository.findById(id);
        if (playerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }

        if (request.getClubId() != null &&
                clubsRepository.findById(request.getClubId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Club ID");
        }

        Player player = mapper.toPlayer(request, playerOptional.get());
        playersRepository.save(player);
        return mapper.toPlayerResponse(player);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({ Permission.Code.MANAGE_PLAYERS })
    @Transactional
    public void deleteClub(@PathVariable("id") String id) {
        Optional<Player> playerOptional = playersRepository.findById(id);
        if (playerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        playersRepository.deleteById(id);
    }

}
