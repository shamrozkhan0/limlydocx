package com.limlydocx.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

                return httpSecurity

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/css/**" , "/js/**" , "/img/**").permitAll()
                        .requestMatchers("/signup/**", "/login/**", "/users").permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                                .loginPage("/login")
                                .failureUrl("/login?error=true")
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )

                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}