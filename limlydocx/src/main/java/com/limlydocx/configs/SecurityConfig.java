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
                        // Allow static resource
                                .requestMatchers("/css/**","/js/**","/img/**").permitAll()
//                        .requestMatchers("/css/index.css", "/css/login.css", "/js/authentication.js", "/img/**").permitAll()
                        // Authentication URI
                        .requestMatchers("/signup/**", "/login/**", "/users").permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")  // Custom login page
                        .loginProcessingUrl("/login")  // Specify the POST URL for login
                        .defaultSuccessUrl("/hello", true)  // Success page after login
                        .failureUrl("/login?error=true")  // Failure page
                        .permitAll()
                )

                .oauth2Login(oauth -> oauth
//                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
//                                .oidcUserService(this::customOidcUserService) // Hook for user processing
//
//                        )
                                .loginPage("/login")
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )

                .build();
    }

//    private OidcUser customOidcUserService(OidcUserRequest oidcUserRequest) {
//        return customOAuth2UserService.loadUser(userRequest);
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}