package com.bee.chat.repository;

import com.bee.chat.model.Role;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("/db/roles.sql")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void contextLoads() {
        AssertionsForClassTypes.assertThat(entityManager).isNotNull();
    }

    @Test
    void givenName_whenFindByName_thenReturnRoleObject() {
        Optional<Role> role = roleRepository.findByName("ROLE_ADMIN");

        assertThat(role).isNotEmpty();
        assertThat(role.get().getName()).isEqualTo("ROLE_ADMIN");
    }


}
