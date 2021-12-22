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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Leagues.PATH)
public class LeaguesController {

    private final LeaguesRepository leaguesRepository;
    private final QueryParser<League> queryParser;
    private final LeaguesMapper mapper;

    @GetMapping
    public Page<LeagueResponse> enumerateLeagues(HttpServletRequest request) throws QueryParseException {
        return leaguesRepository.findAll(queryParser.parse(request.getQueryString()))
                .map(mapper::toLeagueResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({ Permission.Code.MANAGE_LEAGUES })
    public LeagueResponse registerLeague(@Valid @RequestBody RegisterLeagueRequest request) {
        League league = mapper.toLeague(request);
        league.setId(StringUtils.uuid());
        leaguesRepository.save(league);
        return mapper.toLeagueResponse(league);
    }

    @PatchMapping("/{id}")
    @RolesAllowed({ Permission.Code.MANAGE_LEAGUES })
    public LeagueResponse patchLeague(@PathVariable("id") String id, @Valid @RequestBody PatchLeagueRequest request) {
        Optional<League> leagueOptional = leaguesRepository.findById(id);
        if (leagueOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League not found");
        }

        League league = mapper.toLeague(request, leagueOptional.get());
        leaguesRepository.save(league);
        return mapper.toLeagueResponse(league);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({ Permission.Code.MANAGE_LEAGUES })
    public void deleteLeague(@PathVariable("id") String id) {
        Optional<League> leagueOptional = leaguesRepository.findById(id);
        if (leagueOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League not found");
        }
        leaguesRepository.deleteById(id);
    }

}
