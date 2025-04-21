package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.dto.LeagueResponse;
import com.ksonni.footballdb.leagues.dto.PatchLeagueRequest;
import com.ksonni.footballdb.leagues.dto.RegisterLeagueRequest;
import com.ksonni.footballdb.leagues.services.LeaguesMapper;
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
@RequestMapping(value = RoutesConfig.Leagues.PATH)
@Tag(name = "Leagues", description = "Manage leagues")
public class LeaguesController {

    private final LeaguesRepository leaguesRepository;
    private final LeaguesMapper leaguesMapper;

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
    @Operation(
        summary = "Register a new league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
    )
    public LeagueResponse registerLeague(final @Valid @RequestBody RegisterLeagueRequest request) {
        final League league = leaguesMapper.toLeague(request);
        league.setId(StringUtils.uuid());
        leaguesRepository.save(league);
        log.info("created league {}", league.getId());
        return leaguesMapper.toLeagueResponse(league);
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
    @Operation(
        summary = "Update an existing league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
    )
    public LeagueResponse patchLeague(final @PathVariable("id") String id,
                                      final @Valid @RequestBody PatchLeagueRequest request) {
        final Optional<League> leagueOptional = leaguesRepository.findById(id);
        if (leagueOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League not found");
        }

        final League league = leaguesMapper.toLeague(request, leagueOptional.get());
        leaguesRepository.save(league);
        log.info("updated league {}", league.getId());
        return leaguesMapper.toLeagueResponse(league);
    }

    /**
     * Delete a league.
     *
     * @param id id of the league to delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(Permission.Compound.DELETE_LEAGUES)
    @Transactional
    @Operation(
        summary = "Delete an existing league",
        description = DocUtils.PERMISSIONS + Permission.Code.MANAGE_LEAGUES
            + DocUtils.SEPARATOR + Permission.Code.MANAGE_CLUBS
            + DocUtils.SEPARATOR + Permission.Code.MANAGE_PLAYERS
            + DocUtils.LINE_SEPARATOR + "Delete a league and all its clubs and players."
    )
    public void deleteLeague(final @PathVariable("id") String id) {
        final Optional<League> leagueOptional = leaguesRepository.findById(id);
        if (leagueOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League not found");
        }
        log.info("deleted league {}", id);
        leaguesRepository.deleteById(id);
    }

}
