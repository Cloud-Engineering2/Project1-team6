package com.cloud6.place.security;

public class SecurityEndpoints {

    public static final String[] PUBLIC_GET_ENDPOINTS = {
        "/api/auth/**",
        "/test/**",
        "/api/places/**",
        "/api/reviews/**",
        "/api/users/**"
    };

    public static final String[] AUTHENTICATED_POST_ENDPOINTS = {
        "/api/places",
        "/api/reviews"
    };

    public static final String[] AUTHENTICATED_PUT_ENDPOINTS = {
        "/api/places/**",
        "/api/reviews/**",
        "/api/users/**"
    };

    public static final String[] AUTHENTICATED_DELETE_ENDPOINTS = {
        "/api/places/**",
        "/api/reviews/**",
        "/api/users/**"
    };
}

