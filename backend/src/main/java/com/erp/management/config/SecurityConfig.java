package com.erp.management.config;

import com.erp.management.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/actuator/health").permitAll()
                .requestMatchers("/api/auth/users").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/hr/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "HR")
                .requestMatchers("/api/finance/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "FINANCE")
                .requestMatchers("/api/procurement/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "PROCUREMENT")
                .anyRequest().authenticated())
            .httpBasic(basic -> {})
            .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
