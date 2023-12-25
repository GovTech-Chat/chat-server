package com.bee.chat.controller;

import com.bee.chat.dto.UserDto;
import com.bee.chat.mapper.UserMapper;
import com.bee.chat.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Autowired
    private UserMapper userMapper;

    @Test
    void create_shouldCreateSuccessfully(){
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("First Name 1");
        userDto.setLastName("Last Name 1");
        userDto.setUsername("Username1");
        userDto.setEnabled(true);

        when(userService.create(any())).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.create(userDto);
        UserDto actual = response.getBody();

        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(userDto, actual),
                () -> {
                    assert actual != null;
                    assertEquals(userDto.getFirstName(), actual.getFirstName());
                }
        );
    }
}
