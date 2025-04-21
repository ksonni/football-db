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
import com.ksonni.footballdb.users.domain.Permission;
import com.ksonni.footballdb.utils.DocUtils;
import com.ksonni.footballdb.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.Clubs.PATH)
@Tag(name = "Clubs", description = "Manage clubs")
public class ClubsController {

    private final LeaguesRepository leaguesRepository;
    private final ClubsRepository clubsRepository;
    private final ClubsMapper clubsMapper;

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
    @Operation(
        summary = "Register a new club",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
    )
    public ClubResponse registerClub(final @Valid @RequestBody RegisterClubRequest request) {
        final Optional<League> league = leaguesRepository.findById(request.getLeagueId());
        if (league.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid league ID");
        }

        final Club club = clubsMapper.toClub(request);
        club.setId(StringUtils.uuid());
        clubsRepository.save(club);
        log.info("created club {}", club.getId());
        return clubsMapper.toClubResponse(club);
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
    @Operation(
        summary = "Update an existing club",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
    )
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

        final Club club = clubsMapper.toClub(request, clubOptional.get());
        clubsRepository.save(club);
        log.info("updated club {}", club.getId());
        return clubsMapper.toClubResponse(club);
    }

    /**
     * Delete a club.
     *
     * @param id id of the club to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(Permission.Compound.DELETE_CLUBS)
    @Transactional
    @Operation(
        summary = "Delete an existing club",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_CLUBS
            + DocUtils.SEPARATOR + Permission.Code.MANAGE_PLAYERS
            + DocUtils.LINE_SEPARATOR + "Delete a club and all its players."
    )
    public void deleteClub(final @PathVariable("id") String id) {
        final Optional<Club> clubOptional = clubsRepository.findById(id);
        if (clubOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found");
        }
        clubsRepository.deleteById(id);
        log.info("deleted club {}", id);
    }

}
