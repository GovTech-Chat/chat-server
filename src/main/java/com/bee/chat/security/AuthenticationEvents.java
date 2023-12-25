package com.bee.chat.security;

import com.bee.chat.service.UserAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserAttemptService userAttemptService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        log.debug("AuthenticationEvents onSuccess");
        final String username = success.getAuthentication().getName();

        log.debug("username: {}", username);
        log.debug("ip: {}", getClientIP());

        userAttemptService.resetFailAttempts(username, getClientIP());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        log.debug("AuthenticationEvents onFailure");

        final String username = failures.getAuthentication().getName();

        log.debug("username: {}", username);
        log.debug("ip: {}", getClientIP());

        userAttemptService.updateFailAttempts(username, getClientIP());

    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
