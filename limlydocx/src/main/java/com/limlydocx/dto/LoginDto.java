package com.limlydocx.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class LoginDto  {

    @NotBlank(message = "Write email")
    private String email;

    @NotBlank(message = "write password")
    private String password;

    public void setPassword(@NotBlank(message = "write password") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Write email") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Write email") String email) {
        this.email = email;
    }



}
