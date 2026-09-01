package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.response.UserResponse;
import com.ramennsama.springboot.lms.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "provider", expression = "java(user.getProvider().name())")
    UserResponse toUserResponse(User user);
}
