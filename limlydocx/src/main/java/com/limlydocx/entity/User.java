package com.limlydocx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "You forget to write name")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name can only contain letters")
    @Column
    private String name;

    @NotBlank(message = "Username will be used as your unique identity")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and _ ")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "write your gmail")
    @Email(message = "provide a valid gmail")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Write a password")
//    @Size(min = 8, max = 20)
    @Column
    private String password;

    @Column
    private LocalDateTime joinedOn;

    // for testing
    @Column
    private String actualPassword;


    public User(String name, String username, String email, String password, LocalDateTime joinedOn, String acutalPassword) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.joinedOn = joinedOn;
        this.actualPassword = actualPassword;
    }

    public User() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotBlank(message = "You forget to write name") @Pattern(regexp = "^[a-zA-Z]+$", message = "Name can only contain letters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "You forget to write name") @Pattern(regexp = "^[a-zA-Z]+$", message = "Name can only contain letters") String name) {
        this.name = name;
    }

    @Override
    public @NotBlank(message = "Username will be used as your unique identity") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and _ ") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username will be used as your unique identity") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and _ ") String username) {
        this.username = username;
    }

    public @NotBlank(message = "write your gmail") @Email(message = "provide a valid gmail") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "write your gmail") @Email(message = "provide a valid gmail") String email) {
        this.email = email;
    }

    @Override
    public @NotBlank(message = "Write a password")
//    @Size(min = 8, max = 20)
    String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Write a password") @Size(min = 8, max = 20) String password) {
        this.password = password;
    }

    public LocalDateTime getJoinedOn() {
        return joinedOn;
    }

    public void setJoinedOn(LocalDateTime joinedOn) {
        this.joinedOn = joinedOn;
    }

    public String getActualPassword() {
        return actualPassword;
    }

    public void setActualPassword(String actualPassword) {
        this.actualPassword = actualPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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
