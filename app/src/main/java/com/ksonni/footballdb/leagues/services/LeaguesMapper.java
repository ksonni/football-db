package com.ksonni.footballdb.leagues.services;

import com.ksonni.footballdb.config.MapStructConfig;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.dto.LeagueResponse;
import com.ksonni.footballdb.leagues.dto.PatchLeagueRequest;
import com.ksonni.footballdb.leagues.dto.RegisterLeagueRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface LeaguesMapper {

    /**
     * Generated LeaguesMapper instance.
     */
    LeaguesMapper INSTANCE = Mappers.getMapper(LeaguesMapper.class);

    /**
     * Maps League to LeagueResponse DTO.
     *
     * @param league League
     * @return Mapped LeagueResponse
     */
    LeagueResponse toLeagueResponse(League league);

    /**
     * Maps RegisterLeagueRequest DTO to League.
     *
     * @param request RegisterLeagueRequest
     * @return Mapped player
     */
    @Mapping(target = "id", ignore = true)
    League toLeague(RegisterLeagueRequest request);

    /**
     * Maps a PatchLeagueRequest DTO to update an existing League.
     *
     * @param request PatchLeagueRequest
     * @param league  the existing League
     * @return updated League
     */
    @Mapping(target = "id", ignore = true)
    League toLeague(PatchLeagueRequest request, @MappingTarget League league);

}
