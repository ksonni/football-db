package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.config.MapStructConfig;
import com.ksonni.footballdb.generated.ql.QLRole;
import com.ksonni.footballdb.generated.ql.QLUser;
import com.ksonni.footballdb.generated.ql.QLUserPage;
import com.ksonni.footballdb.queryparser.PageResult;
import com.ksonni.footballdb.users.domain.Role;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.dto.RegisterUserRequest;
import com.ksonni.footballdb.users.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface UsersMapper {

    /**
     * Generated UsersMapper instance.
     */
    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);

    /**
     * Maps RegisterUserRequest DTO to User.
     *
     * @param request RegisterUserRequest
     * @return Mapped user
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(RegisterUserRequest request);

    /**
     * Maps User to UserResponse DTO.
     *
     * @param user User
     * @return Mapped UserResponse
     */
    UserResponse toUserResponse(User user);

    /**
     * Maps a User to QLUser DTO.
     *
     * @param user User
     * @return mapped response
     */
    QLUser toUserQL(User user);


    /**
     * Maps a PageResult to QLUserPage DTO.
     *
     * @param page PageResult
     * @return QLUserPage
     */
    QLUserPage toQLPage(PageResult<User> page);

    /**
     * Maps a QLRole DTO to Role.
     *
     * @param role QLRole
     * @return Role
     */
    Role toRole(QLRole role);
}
