package com.bee.chat.service;

import com.bee.chat.model.User;
import com.bee.chat.model.UserAttempt;
import com.bee.chat.repository.UserAttemptRepository;
import com.bee.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserAttemptService {

    @Value("${app.maxLoginAttempts}")
    private int maxLoginAttempts;

    @Autowired
    private UserAttemptRepository userAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<UserAttempt> getUserAttempt(String username) {
        return userAttemptRepository.findByUsername(username);
    }

    public void updateFailAttempts(String username, String ipAddress) {
        Optional<UserAttempt> userAttemptOptional = getUserAttempt(username);

        if (userAttemptOptional.isEmpty()) {
            UserAttempt userAttempt = UserAttempt.builder()
                    .username(username)
                    .build();
            saveAttempt(userAttempt, 1, ipAddress);
        } else {
            UserAttempt userAttempt = userAttemptOptional.get();
            saveAttempt(userAttempt, userAttempt.getAttempt() + 1, ipAddress);

            if (userAttempt.getAttempt() >= maxLoginAttempts) {
                lockUser(username);
            }

        }
    }

    public void resetFailAttempts(String username, String ipAddress) {
        Optional<UserAttempt> userAttemptOptional = getUserAttempt(username);

        userAttemptOptional.ifPresent(userAttempt -> saveAttempt(userAttempt, 0, ipAddress));
    }

    private void saveAttempt(UserAttempt userAttempt, int attempts, String ipAddress) {
        userAttempt.setAttempt(attempts);
        userAttempt.setIpAddress(ipAddress);
        userAttempt.setLastModifiedOn(LocalDateTime.now());
        userAttemptRepository.save(userAttempt);
    }

    private void lockUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAccountLocked(true);
            userRepository.save(user);
        }
    }
}
