package com.bee.chat.security;

import com.bee.chat.dto.request.JwtRefreshRequest;
import com.bee.chat.dto.request.SignUpRequest;
import com.bee.chat.dto.request.SigninRequest;
import com.bee.chat.dto.response.JwtAuthenticationResponse;
import com.bee.chat.exception.ResourceNotFoundException;
import com.bee.chat.exception.TokenRefreshException;
import com.bee.chat.model.JwtToken;
import com.bee.chat.model.Role;
import com.bee.chat.model.User;
import com.bee.chat.repository.JwtTokenRepository;
import com.bee.chat.repository.RoleRepository;
import com.bee.chat.repository.UserRepository;
import com.bee.chat.util.EmailUtil;
import com.bee.chat.util.FileUtil;
import com.bee.chat.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenRepository refreshTokenRepository;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private FileUtil fileUtil;

    public User signup(SignUpRequest request) {
        Set<Role> roles = request.getRoles().stream().map(x -> {
            return roleRepository.findByName(x)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", x));
        }).collect(Collectors.toSet());

        String password = PasswordUtil.generatePassword();
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(password))
                .isEnabled(true)
                .roles(roles)
                .build();


        CompletableFuture.runAsync(() -> emailUtil.sendNewUserEmail(user, password));

        return userRepository.save(user);
    }

    public JwtToken signIn(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Authentication fail."));

        //TODO
        // If request not from local front end then check IP

        return jwtService.generateToken(user, userDetailsService.loadUserByUsername(user.getUsername()));
    }

//    public void signOut() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails userDetails = (UserDetails) auth.getPrincipal();
//        String username = userDetails.getUsername();
//
//        Optional<User> optionalUser = userRepository.findByUsername(username);
//
//        optionalUser.ifPresent(user -> jwtService.deleteRefreshTokenByUser(user));
//    }

    public JwtAuthenticationResponse refreshToken(JwtRefreshRequest request) {
        return jwtService.findRefreshTokenByToken(request.getRefreshToken())
                .map(jwtService::verifyRefreshTokenExpiration)
                .map(JwtToken::getUser)
                .map(user -> {
                    JwtToken jwtToken = jwtService.generateToken(user, userDetailsService.loadUserByUsername(user.getUsername()));
                    return JwtAuthenticationResponse.builder().accessToken(jwtToken.getAccessToken()).refreshToken(jwtToken.getRefreshToken()).build();
                })
                .orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(),
                        "Invalid refresh token!"));
    }
}
