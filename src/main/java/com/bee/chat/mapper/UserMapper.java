package com.bee.chat.mapper;

import com.bee.chat.dto.RoleDto;
import com.bee.chat.dto.UserDto;
import com.bee.chat.model.Role;
import com.bee.chat.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(source = "enabled", target = "isEnabled")
    @Mapping(source = "accountExpired", target = "isAccountExpired")
    @Mapping(source = "accountLocked", target = "isAccountLocked")
    @Mapping(source = "credentialsExpired", target = "isCredentialsExpired")
    User toEntity(UserDto dto);

    RoleDto roleToDto(Role role);
}
