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

    LeaguesMapper INSTANCE = Mappers.getMapper(LeaguesMapper.class);

    LeagueResponse toLeagueResponse(League league);

    @Mapping(target = "id", ignore = true)
    League toLeague(RegisterLeagueRequest request);

    @Mapping(target = "id", ignore = true)
    League toLeague(PatchLeagueRequest request, @MappingTarget League league);

}
