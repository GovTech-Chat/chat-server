package com.bee.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String existingPassword;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
}
