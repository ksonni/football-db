package com.ksonni.footballdb.clubs.services;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.dto.ClubResponse;
import com.ksonni.footballdb.clubs.dto.PatchClubRequest;
import com.ksonni.footballdb.clubs.dto.RegisterClubRequest;
import com.ksonni.footballdb.config.MapStructConfig;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper(config = MapStructConfig.class)
public interface ClubsMapper {

    ClubsMapper INSTANCE = Mappers.getMapper(ClubsMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "overallRating", defaultValue = "0")
    @Mapping(target = "attackRating", defaultValue = "0")
    @Mapping(target = "midfieldRating", defaultValue = "0")
    @Mapping(target = "defenseRating", defaultValue = "0")
    @Mapping(target = "transferBudget", defaultValue = "0")
    @Mapping(target = "domesticPrestige", defaultValue = "0")
    @Mapping(target = "internationalPrestige", defaultValue = "0")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    Club toClub(RegisterClubRequest request);

    @Mapping(target = "id", ignore = true)
    Club toClub(PatchClubRequest request, @MappingTarget Club club);

    ClubResponse toClubResponse(Club club);

}
