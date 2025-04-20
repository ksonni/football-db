package com.ksonni.footballdb.players.services;

import com.ksonni.footballdb.config.MapStructConfig;
import com.ksonni.footballdb.generated.ql.*;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Position;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.players.dto.PatchPlayerRequest;
import com.ksonni.footballdb.players.dto.PlayerResponse;
import com.ksonni.footballdb.players.dto.RegisterPlayerRequest;
import com.ksonni.footballdb.queryparser.PageResult;
import org.mapstruct.*;
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

    /**
     * Maps a Player to QLPlayer DTO.
     *
     * @param player Player
     * @return mapped response
     */
    QLPlayer toPlayerQL(Player player);

    /**
     * Maps a PageResult to QLPlayerPage DTO.
     *
     * @param page QLPlayerPage
     * @return QLPlayerPage
     */
    QLPlayerPage toQLPage(PageResult<Player> page);

    /**
     * Maps a QLWorkRate DTO to WorkRate.
     *
     * @param workRate QLWorkRate
     * @return WorkRate
     */
    WorkRate toWorkRate(QLWorkRate workRate);

    /**
     * Maps a QLSide DTO to Side.
     *
     * @param side QLSide
     * @return QLSide
     */
    Side toSide(QLSide side);

    /**
     * Maps a QLPosition DTO to Position.
     *
     * @param position QLPosition
     * @return QLPosition
     */
    Position toPosition(QLPosition position);
}
