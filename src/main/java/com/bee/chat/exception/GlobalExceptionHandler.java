package com.bee.chat.exception;

import com.bee.chat.dto.response.AppResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public AppResponse handleException(Exception e) {
        return createResponse(e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public AppResponse handleException(BadCredentialsException e) {
        return createResponse(e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public AppResponse handleException(ExpiredJwtException e) {
        return createResponse(e.getMessage());
    }

    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public AppResponse handleException(TokenRefreshException e) {
        return createResponse(e.getMessage());
    }

    private AppResponse createResponse(String message) {
        return AppResponse.builder()
                .status("error")
                .message(message)
                .build();
    }
}
