package com.springboot.intro.mapper;

import com.springboot.intro.config.MapperConfig;
import com.springboot.intro.dto.request.UserRegistrationRequestDto;
import com.springboot.intro.dto.response.UserResponseDto;
import com.springboot.intro.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto userRegistrationRequestDto);
}
