package com.ksonni.footballdb.users.services;

import com.ksonni.footballdb.config.MapStructConfig;
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
    @Mapping(target = "authMethod", ignore = true)
    User toUser(RegisterUserRequest request);

    /**
     * Maps User to UserResponse DTO.
     *
     * @param user User
     * @return Mapped UserResponse
     */
    UserResponse toUserResponse(User user);

}
