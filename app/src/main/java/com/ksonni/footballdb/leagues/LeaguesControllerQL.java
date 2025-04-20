package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.generated.ql.*;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.services.LeaguesMapper;
import com.ksonni.footballdb.leagues.services.LeaguesRepository;
import com.ksonni.footballdb.qlquery.FilterParseException;
import com.ksonni.footballdb.qlquery.FilterParser;
import com.ksonni.footballdb.qlquery.SortParseException;
import com.ksonni.footballdb.qlquery.SortParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * GraphQL mappings to query leagues.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LeaguesControllerQL {

    private final LeaguesMapper leaguesMapper;
    private final LeaguesRepository leaguesRepository;
    private final FilterParser<League, QLLeagueFilter> leaguesFilterParser;
    private final SortParser<QLLeagueSort> leaguesSortParser;

    /**
     * Get a league by id.
     *
     * @param id id of the league
     * @return league with the id
     */
    @QueryMapping
    @Transactional(readOnly = true)
    public QLLeague league(@Argument final String id) {
        final var league = leaguesRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "league not found")
        );
        log.info("returning league {}", league.getId());
        return leaguesMapper.toLeagueQL(league);
    }

    /**
     * Query leagues with filtering, sorting & pagination.
     *
     * @param filter filter to select leagues
     * @param sort specifies sort order for results
     * @param page configures pagination
     * @return paginated list of leagues matching the filter.
     */
    @QueryMapping
    @Transactional(readOnly = true)
    public QLLeaguePage leagues(
        @Argument final QLLeagueFilter filter,
        @Argument final QLLeagueSort sort,
        @Argument final QLPagination page
    ) throws FilterParseException, SortParseException {
        final var results = leaguesRepository.findAllResults(
            leaguesFilterParser.parse(filter).orElse(null),
            leaguesSortParser.parse(sort, page)
        );
        log.info("returning {} leagues", results.content().size());
        return leaguesMapper.toQLPage(results);
    }

}
