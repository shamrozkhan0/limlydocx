package com.limlydocx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name only contains letters, and not numbers or letter")
    private String name;


    @NotBlank(message = "You didn't write username")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
            message = "username only contains letters and numbers and _")
    @Column(unique = true)
    private String username;


    @NotBlank(message = "You didn't write email")
    @Email(message = "provide a valid email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format")
//    @Column(unique = true)
    private String email;


    @NotBlank(message = "You didn't write password")
    @Size(min = 6, message = "password minimum size is 6 character")
    private String password;


    @Column(unique = false)
    private LocalDateTime joinedOn;


    private String actualPassword;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // NOt roles for now
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
