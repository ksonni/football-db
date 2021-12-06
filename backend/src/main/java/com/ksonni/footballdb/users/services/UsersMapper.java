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

    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);

    @Mapping(target = "id", ignore = true)
    User toUser(RegisterUserRequest request);

    UserResponse toUserResponse(User user);

}
