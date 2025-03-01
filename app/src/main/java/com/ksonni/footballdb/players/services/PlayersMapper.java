package com.ksonni.footballdb.players.services;

import com.ksonni.footballdb.config.MapStructConfig;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.dto.PatchPlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerResponse;
import com.ksonni.footballdb.players.dto.RegisterPlayerRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface PlayersMapper {

    /**
     * Generated PlayersMapper instance.
     */
    PlayersMapper INSTANCE = Mappers.getMapper(PlayersMapper.class);

    /**
     * Maps RegisterPlayerRequest DTO to Player.
     *
     * @param request RegisterPlayerRequest
     * @return Mapped player
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    Player toPlayer(RegisterPlayerRequest request);

    /**
     * Maps PatchPlayerRequest DTO to update an existing Player.
     *
     * @param request PatchPlayerRequest
     * @param player  the existing Player
     * @return updated Player
     */
    @Mapping(target = "id", ignore = true)
    Player toPlayer(PatchPlayerRequest request, @MappingTarget Player player);

    /**
     * Maps a Player to PlayerResponse DTO.
     *
     * @param player Player
     * @return mapped response
     */
    PlayerResponse toPlayerResponse(Player player);

}
