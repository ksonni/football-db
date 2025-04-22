package com.ksonni.footballdb.players;

import com.ksonni.footballdb.generated.ql.*;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.services.PlayersMapper;
import com.ksonni.footballdb.players.services.PlayersRepository;
import com.ksonni.footballdb.query.FilterParseException;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.SortParseException;
import com.ksonni.footballdb.query.SortParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * GraphQL mappings to query players.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class PlayersControllerQL {

    private final PlayersMapper playersMapper;
    private final PlayersRepository playersRepository;
    private final FilterParser<Player, QLPlayerFilter> playersFilterParser;
    private final SortParser<QLPlayerSort> playersSortParser;

    /**
     * Get a player by id.
     *
     * @param id id of the player
     * @return player with the id
     */
    @QueryMapping
    @Transactional(readOnly = true)
    public Optional<QLPlayer> player(@Argument final String id) {
        log.info("finding player {}", id);
        return playersRepository.findById(id).map(playersMapper::toPlayerQL);
    }

    /**
     * Query players with filtering, sorting & pagination.
     *
     * @param filter filter to select players
     * @param sort specifies sort order for results
     * @param page configures pagination
     * @return paginated list of players matching the filter.
     */
    @QueryMapping
    @Transactional(readOnly = true)
    public QLPlayerPage players(
        @Argument final QLPlayerFilter filter,
        @Argument final QLPlayerSort sort,
        @Argument final QLPagination page
    ) throws FilterParseException, SortParseException {
        final var results = playersRepository.findAllResults(
            playersFilterParser.parse(filter).orElse(null),
            playersSortParser.parse(sort, page)
        );
        log.info("returning {} players", results.content().size());
        return playersMapper.toQLPage(results);
    }

}
