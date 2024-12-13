package com.cloud6.place.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // JWT Authentication Filter 생성
        JwtAuthenticationFilter jwtAuthenticationFilter = 
            new JwtAuthenticationFilter(jwtTokenProvider, authenticationManager(http), customUserDetailsService);

     // HTTP 보안 설정
        http.csrf().disable()
        	.cors()
        	.and()
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/**").permitAll()
                .requestMatchers("/login/**").permitAll()
                .requestMatchers("/logout/**").permitAll()
                .requestMatchers("/signup/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                .requestMatchers(HttpMethod.GET, SecurityEndpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, SecurityEndpoints.AUTHENTICATED_POST_ENDPOINTS).authenticated()
                .requestMatchers(HttpMethod.PUT, SecurityEndpoints.AUTHENTICATED_PUT_ENDPOINTS).authenticated()
                .requestMatchers(HttpMethod.DELETE, SecurityEndpoints.AUTHENTICATED_DELETE_ENDPOINTS).authenticated()
                .anyRequest().authenticated()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}
