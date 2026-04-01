package com.workintech.s19d2.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(
            @Qualifier("authenticationService") UserDetailsService userDetailsService
    ) {
        // @Qualifier sayesinde tam olarak senin AuthenticationService'ini enjekte eder
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/workintech/auth/**").permitAll();
                    // Rol kontrolleri (USER/ADMIN)
                    auth.requestMatchers(HttpMethod.GET, "/workintech/accounts/**").hasAnyAuthority("USER", "ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/workintech/accounts/**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/workintech/accounts/**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/workintech/accounts/**").hasAuthority("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}