package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.services.ClubsMapper;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
import com.ksonni.footballdb.generated.ql.*;
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
 * GraphQL mappings to query clubs.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ClubsControllerQL {

    private final ClubsMapper clubsMapper;
    private final ClubsRepository clubsRepository;
    private final FilterParser<Club, QLClubFilter> clubsFilterParser;
    private final SortParser<QLClubSort> clubsSortParser;

    /**
     * Get a club by id.
     *
     * @param id id of the club
     * @return club with the id
     */
    @QueryMapping
    @Transactional(readOnly = true)
    public Optional<QLClub> club(@Argument final String id) {
        log.info("finding club {}", id);
        return clubsRepository.findById(id).map(clubsMapper::toClubQL);
    }

    /**
     * Query clubs with filtering, sorting & pagination.
     *
     * @param filter filter to select clubs
     * @param sort specifies sort order for results
     * @param page configures pagination
     * @return paginated list of clubs matching the filter.
     */
    @QueryMapping
    @Transactional(readOnly = true)
    public QLClubPage clubs(
        @Argument final QLClubFilter filter,
        @Argument final QLClubSort sort,
        @Argument final QLPagination page
    ) throws FilterParseException, SortParseException {
        final var results = clubsRepository.findAllResults(
            clubsFilterParser.parse(filter).orElse(null),
            clubsSortParser.parse(sort, page)
        );
        log.info("returning {} clubs", results.content().size());
        return clubsMapper.toQLPage(results);
    }

}
