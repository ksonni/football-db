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

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Clubs.PATH)
@ClubsControllerDoc
public class ClubsController {

    private final LeaguesRepository leaguesRepository;
    private final ClubsRepository clubsRepository;
    private final QueryParser<Club> queryParser;
    private final ClubsMapper mapper;

    /**
     * Query clubs.
     *
     * @param request HTTP request.
     * @return Paginated list of clubs.
     * @throws QueryParseException if the query is not valid
     */
    @GetMapping
    @Transactional(readOnly = true)
    @EnumerateClubsDoc
    public Page<ClubResponse> enumerateClubs(final HttpServletRequest request) throws QueryParseException {
        final String query = request.getQueryString();
        log.info("Processing query: {}", query);
        return clubsRepository.findAll(queryParser.parse(query)).map(mapper::toClubResponse);
    }

    /**
     * Register a new club.
     *
     * @param request club registration request
     * @return the created club
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({Permission.Code.MANAGE_CLUBS})
    @Transactional
    @RegisterClubDoc
    public ClubResponse registerClub(final @Valid @RequestBody RegisterClubRequest request) {
        final Optional<League> league = leaguesRepository.findById(request.getLeagueId());
        if (league.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid league ID");
        }

        final Club club = mapper.toClub(request);
        club.setId(StringUtils.uuid());
        clubsRepository.save(club);
        log.info("created club {}", club.getId());
        return mapper.toClubResponse(club);
    }

    /**
     * Partially update a club.
     *
     * @param id      id of the club to update
     * @param request club update request
     * @return success/error response
     */
    @PatchMapping("/{id}")
    @RolesAllowed({Permission.Code.MANAGE_CLUBS})
    @Transactional
    @PatchClubDoc
    public ClubResponse patchClub(final @PathVariable("id") String id,
                                  final @Valid @RequestBody PatchClubRequest request) {
        final Optional<Club> clubOptional = clubsRepository.findById(id);
        if (clubOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found");
        }

        if (request.getLeagueId() != null
                && leaguesRepository.findById(request.getLeagueId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid League ID");
        }

        final Club club = mapper.toClub(request, clubOptional.get());
        clubsRepository.save(club);
        log.info("updated club {}", club.getId());
        return mapper.toClubResponse(club);
    }

    /**
     * Delete a club.
     *
     * @param id id of the club to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(Permission.Compound.DELETE_CLUBS)
    @Transactional
    @DeleteClubDoc
    public void deleteClub(final @PathVariable("id") String id) {
        final Optional<Club> clubOptional = clubsRepository.findById(id);
        if (clubOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found");
        }
        clubsRepository.deleteById(id);
        log.info("deleted club {}", id);
    }

}
