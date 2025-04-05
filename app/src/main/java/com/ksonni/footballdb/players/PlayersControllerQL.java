package com.ksonni.footballdb.players;

import com.ksonni.footballdb.generated.ql.QLPlayer;
import com.ksonni.footballdb.players.services.PlayersMapper;
import com.ksonni.footballdb.players.services.PlayersRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
@AllArgsConstructor
public class PlayersControllerQL {

    private final PlayersMapper mapper;
    private final PlayersRepository playersRepository;

    /**
     * Resolver to get a player by id.
     *
     * @param id test
     * @return test
     */
    @QueryMapping
    public QLPlayer playerById(@Argument final String id) {
        final var player = playersRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "player not found")
        );
        log.info("returning player {}", player.getId());
        return mapper.toPlayerQL(player);
    }


}
