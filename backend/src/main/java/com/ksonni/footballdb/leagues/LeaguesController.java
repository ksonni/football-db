package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.dto.LeagueResponse;
import com.ksonni.footballdb.leagues.dto.PatchLeagueRequest;
import com.ksonni.footballdb.leagues.dto.RegisterLeagueRequest;
import com.ksonni.footballdb.leagues.services.LeaguesMapper;
import com.ksonni.footballdb.leagues.services.LeaguesRepository;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Leagues.PATH)
@LeaguesControllerDoc
public class LeaguesController {

    private final LeaguesRepository leaguesRepository;
    private final QueryParser<League> queryParser;
    private final LeaguesMapper mapper;

    /**
     * Query leagues.
     *
     * @param request HTTP request.
     * @return Paginated list of leagues.
     * @throws QueryParseException if the query is not valid
     */
    @GetMapping
    @Transactional(readOnly = true)
    @EnumerateLeaguesDoc
    public Page<LeagueResponse> enumerateLeagues(final HttpServletRequest request) throws QueryParseException {
        return leaguesRepository.findAll(queryParser.parse(request.getQueryString()))
                .map(mapper::toLeagueResponse);
    }

    /**
     * Register a new league.
     *
     * @param request league registration request
     * @return the created league
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({Permission.Code.MANAGE_LEAGUES})
    @Transactional
    @RegisterLeagueDoc
    public LeagueResponse registerLeague(final @Valid @RequestBody RegisterLeagueRequest request) {
        final League league = mapper.toLeague(request);
        league.setId(StringUtils.uuid());
        leaguesRepository.save(league);
        log.info("created league {}", league.getId());
        return mapper.toLeagueResponse(league);
    }

    /**
     * Partially update a league.
     *
     * @param id      id of the league to update
     * @param request league update request
     * @return success/error response
     */
    @PatchMapping("/{id}")
    @RolesAllowed({Permission.Code.MANAGE_LEAGUES})
    @Transactional
    @PatchLeagueDoc
    public LeagueResponse patchLeague(final @PathVariable("id") String id,
                                      final @Valid @RequestBody PatchLeagueRequest request) {
        final Optional<League> leagueOptional = leaguesRepository.findById(id);
        if (leagueOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League not found");
        }

        final League league = mapper.toLeague(request, leagueOptional.get());
        leaguesRepository.save(league);
        log.info("updated league {}", league.getId());
        return mapper.toLeagueResponse(league);
    }

    /**
     * Delete a league.
     *
     * @param id id of the league to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(Permission.Compound.DELETE_LEAGUES)
    @Transactional
    @DeleteLeagueDoc
    public void deleteLeague(final @PathVariable("id") String id) {
        final Optional<League> leagueOptional = leaguesRepository.findById(id);
        if (leagueOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League not found");
        }
        log.info("deleted league {}", id);
        leaguesRepository.deleteById(id);
    }

}
