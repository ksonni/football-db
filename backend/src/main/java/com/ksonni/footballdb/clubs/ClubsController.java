package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.dto.ClubResponse;
import com.ksonni.footballdb.clubs.dto.PatchClubRequest;
import com.ksonni.footballdb.clubs.dto.RegisterClubRequest;
import com.ksonni.footballdb.clubs.services.ClubsMapper;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.services.LeaguesRepository;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Clubs.PATH)
public class ClubsController {

    private final LeaguesRepository leaguesRepository;
    private final ClubsRepository clubsRepository;
    private final QueryParser<Club> queryParser;
    private final ClubsMapper mapper;

    @GetMapping
    @Transactional(readOnly = true)
    public Page<ClubResponse> enumerateClubs(HttpServletRequest request) throws QueryParseException {
        return clubsRepository.findAll(queryParser.parse(request.getQueryString()))
                .map(mapper::toClubResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({ Permission.Code.MANAGE_CLUBS })
    @Transactional
    public ClubResponse registerClub(@Valid @RequestBody RegisterClubRequest request) {
        Optional<League> league = leaguesRepository.findById(request.getLeagueId());
        if (league.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid league ID");
        }

        Club club = mapper.toClub(request);
        club.setId(StringUtils.uuid());
        clubsRepository.save(club);
        return mapper.toClubResponse(club);
    }

    @PatchMapping("/{id}")
    @RolesAllowed({ Permission.Code.MANAGE_CLUBS })
    @Transactional
    public ClubResponse patchClub(@PathVariable("id") String id, @Valid @RequestBody PatchClubRequest request) {
        Optional<Club> clubOptional = clubsRepository.findById(id);
        if (clubOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found");
        }

        if (request.getLeagueId() != null &&
            leaguesRepository.findById(request.getLeagueId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid League ID");
        }

        Club club = mapper.toClub(request, clubOptional.get());
        clubsRepository.save(club);
        return mapper.toClubResponse(club);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Permission.Compound.DELETE_CLUBS)
    @Transactional
    public void deleteClub(@PathVariable("id") String id) {
        Optional<Club> clubOptional = clubsRepository.findById(id);
        if (clubOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found");
        }
        clubsRepository.deleteById(id);
    }

}
