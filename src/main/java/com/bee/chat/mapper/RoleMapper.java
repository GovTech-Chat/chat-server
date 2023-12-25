package com.bee.chat.mapper;

import com.bee.chat.dto.RoleDto;
import com.bee.chat.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoleMapper {
    RoleDto toDto(Role user);

    Role toEntity(RoleDto dto);
}
