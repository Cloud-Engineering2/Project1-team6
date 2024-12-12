package com.cloud6.place.security;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

