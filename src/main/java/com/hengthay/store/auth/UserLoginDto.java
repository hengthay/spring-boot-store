package com.hengthay.store.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotBlank(message = "Email is required")
    @Email
    String email;

    @NotBlank(message = "Password is required")
    String password;
}