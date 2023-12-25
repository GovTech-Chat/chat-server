package com.bee.chat.service;

import com.bee.chat.dto.RoleDto;
import com.bee.chat.dto.UserDto;
import com.bee.chat.dto.request.ChangePasswordRequest;
import com.bee.chat.exception.DuplicateResourceException;
import com.bee.chat.exception.ResourceNotFoundException;
import com.bee.chat.mapper.UserMapper;
import com.bee.chat.model.Role;
import com.bee.chat.model.User;
import com.bee.chat.repository.RoleRepository;
import com.bee.chat.repository.UserRepository;
import com.bee.chat.util.EmailUtil;
import com.bee.chat.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final EmailUtil emailUtil;

    private final PasswordEncoder passwordEncoder;

    public UserDto create(UserDto userDto) {

        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());

        if(optionalUser.isPresent()){
            throw new DuplicateResourceException("Username Already Exists for User");
        }

        User user = userMapper.toEntity(userDto);
        user.setRoles(this.getRoles(userDto.getRoles()));

        if(user.isEnabled()){
            String password = PasswordUtil.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            CompletableFuture.runAsync(() -> emailUtil.sendNewUserEmail(user, password));
        }

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    public UserDto update(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userDto.getId()))
        );

        existingUser.setRoles(this.getRoles(userDto.getRoles()));
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());

        User savedUser = userRepository.save(existingUser);
        return userMapper.toDto(savedUser);
    }

    public UserDto get(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userId))
        );
        return userMapper.toDto(user);
    }

    public UserDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails)auth.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userDetails.getUsername()))
        );
        return userMapper.toDto(user);
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toDto)
                .toList();
    }

    public Page<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                        .map(userMapper::toDto);
    }

    public UserDto changePassword(ChangePasswordRequest request) {
        User existingUser = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", String.valueOf(request.getUsername()))
        );

        if(request.getPassword().equals(request.getConfirmPassword())
                && PasswordUtil.validatePassword(request.getPassword())){
            existingUser.setPassword(request.getPassword());
            return userMapper.toDto(userRepository.save(existingUser));
        }

        throw new RuntimeException();
    }

    public UserDto lockUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userDto.getId()))
        );

        existingUser.setAccountLocked(true);

        return userMapper.toDto(userRepository.save(existingUser));
    }

    public UserDto enableUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userDto.getId()))
        );

        existingUser.setEnabled(true);

        return userMapper.toDto(userRepository.save(existingUser));
    }

    public UserDto disableUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userDto.getId()))
        );

        existingUser.setEnabled(false);

        return userMapper.toDto(userRepository.save(existingUser));
    }

    public UserDto unlockUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userDto.getId()))
        );

        existingUser.setAccountLocked(false);

        return userMapper.toDto(userRepository.save(existingUser));
    }

    public void deleteUser(Long userId) {

        userRepository.findById(userId).ifPresentOrElse(
                user
                        -> userRepository.deleteById(user.getId()),
                ()
                        -> {
                    log.debug("Attempt to delete non exist user: {}", userId);
                    throw new ResourceNotFoundException("User", "id", String.valueOf(userId));
                });
    }

    private Set<Role> getRoles(Set<RoleDto> roleDtos) {
        return emptyIfNull(roleDtos).stream().map(r ->
            roleRepository.findByName(r.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", r.getName()))
        ).collect(Collectors.toSet());
    }
}
