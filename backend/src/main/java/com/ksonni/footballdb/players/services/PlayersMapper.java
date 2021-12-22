package com.ksonni.footballdb.players.services;

import com.ksonni.footballdb.config.MapStructConfig;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.dto.PatchPlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerResponse;
import com.ksonni.footballdb.players.dto.RegisterPlayerRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface PlayersMapper {

    PlayersMapper INSTANCE = Mappers.getMapper(PlayersMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    Player toPlayer(RegisterPlayerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    Player toPlayer(PatchPlayerRequest request, @MappingTarget Player player);

    PlayerResponse toPlayerResponse(Player player);

}
