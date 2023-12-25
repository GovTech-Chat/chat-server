package com.bee.chat.controller;

import com.bee.chat.dto.UserDto;
import com.bee.chat.dto.request.ChangePasswordRequest;
import com.bee.chat.dto.response.AppResponse;
import com.bee.chat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<AppResponse> getAll(@RequestParam("current") int page,
                                              @RequestParam("pageSize") int pageSize,
                                              @RequestParam(name = "sort", required = false) String sortBy) {
        Sort sort = StringUtils.isBlank(sortBy) ? Sort.unsorted() : Sort.by(sortBy);

        Pageable paging = PageRequest.of(page - 1, pageSize, sort);
        Page<UserDto> users = userService.getAll(paging);
        AppResponse res = AppResponse.builder()
                .data(users.getContent())
                .pageSize(pageSize)
                .currentPage(page)
                .total(users.getTotalElements())
                .status("Ok")
                .build();

        return ResponseEntity.ok(res);
    }

    @Operation(
            summary = "Create User REST API",
            description = "Create User REST API is used to save user in a database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    // build create User REST API
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto user) {
        UserDto savedUser = userService.create(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserDto> update(@PathVariable("id") long id, @Valid @RequestBody UserDto user) {
        UserDto savedUser = userService.update(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/change-password")
    public ResponseEntity<UserDto> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UserDto savedUser = userService.changePassword(request);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get User By ID REST API",
            description = "Get User By ID REST API is used to get a single user from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    @GetMapping("{id}")
    public ResponseEntity<UserDto> get(@PathVariable("id") Long userId) {
        UserDto user = userService.get(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Get User By ID REST API",
            description = "Get User By ID REST API is used to get a single user from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    @GetMapping("/me")
    public ResponseEntity<AppResponse> getCurrentUser() {
        AppResponse res = AppResponse.builder()
                .data(userService.getCurrentUser())
                .status("Ok")
                .build();

        return ResponseEntity.ok(res);
    }
}
