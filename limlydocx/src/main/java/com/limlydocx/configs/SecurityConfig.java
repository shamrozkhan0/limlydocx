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
                        // Resource permit
                        .requestMatchers("/css/**" , "/js/**" , "/img/**").permitAll()
                        .requestMatchers("/signup/**", "/login/**", "/users", "/docs").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                                .loginPage("/login")
                                .defaultSuccessUrl("/dashboard",true )
                                .failureUrl("/login?error=true")
                )
                 .oauth2Login(oauth2 -> oauth2
                                .loginPage("/login")
                                .defaultSuccessUrl("/dashboard", true)
                 )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                ).sessionManagement(session -> session
                                .maximumSessions(1)
                                .expiredUrl("/login?expired")
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