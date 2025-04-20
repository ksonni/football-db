package com.ksonni.footballdb.clubs.services;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.dto.ClubResponse;
import com.ksonni.footballdb.clubs.dto.PatchClubRequest;
import com.ksonni.footballdb.clubs.dto.RegisterClubRequest;
import com.ksonni.footballdb.config.MapStructConfig;
import com.ksonni.footballdb.generated.ql.QLClub;
import com.ksonni.footballdb.generated.ql.QLClubPage;
import com.ksonni.footballdb.queryparser.PageResult;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper(config = MapStructConfig.class)
public interface ClubsMapper {

    /**
     * Generated ClubsMapper instance.
     */
    ClubsMapper INSTANCE = Mappers.getMapper(ClubsMapper.class);

    /**
     * Maps RegisterClubRequest DTO to Club.
     *
     * @param request RegisterClubRequest
     * @return Mapped club
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    Club toClub(RegisterClubRequest request);

    /**
     * Maps a PatchClubRequest DTO to update an existing Club.
     *
     * @param request PatchClubRequest
     * @param club    the existing Club
     * @return updated Club
     */
    @Mapping(target = "id", ignore = true)
    Club toClub(PatchClubRequest request, @MappingTarget Club club);

    /**
     * Maps a Club to ClubResponse DTO.
     *
     * @param club Club
     * @return Mapped ClubResponse
     */
    ClubResponse toClubResponse(Club club);

    /**
     * Maps a Club to QLClub DTO.
     *
     * @param club Club
     * @return mapped response
     */
    QLClub toClubQL(Club club);

    /**
     * Maps a PageResult to QLClubPage DTO.
     *
     * @param page QLClubPage
     * @return QLClubPage
     */
    QLClubPage toQLPage(PageResult<Club> page);

}
