package com.bee.chat.mapper;

import com.bee.chat.dto.RoleDto;
import com.bee.chat.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void mapToEntity(){
        RoleDto roleDto = new RoleDto(
                1L, "ROLE_ADMIN"
        );

        Role role = roleMapper.toEntity(roleDto);

        assertThat(role.getId()).isEqualTo(roleDto.getId());
        assertThat(role.getName()).isEqualTo(roleDto.getName());
        assertThat(role.getUsers()).isNullOrEmpty();
    }

    @Test
    void mapToDto(){
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        RoleDto roleDto = roleMapper.toDto(role);

        assertThat(roleDto.getId()).isEqualTo(role.getId());
        assertThat(roleDto.getName()).isEqualTo(role.getName());
    }
}
