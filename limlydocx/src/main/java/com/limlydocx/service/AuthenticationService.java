package com.limlydocx.service;

import com.limlydocx.configs.SecurityConfig;
import com.limlydocx.entity.User;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GlobalVariable globalVariable;
    private final AuthenticationManager authenticationManager;


    /**
     * Check User Credentials For Register
     *
     * @param user
     * @param result
     * @param redirectAttributes
     */
    public void registerUser(User user, BindingResult result, RedirectAttributes redirectAttributes) {
        log.info("Processing the user register");
        this.globalVariable.checkFeildError(result);

        this.checkUserIsPresentByEmail(user.getEmail());
        this.CheckUserIsPresentByUsername(user.getUsername());


        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        String formatedUsername = StringUtils.cleanPath(user.getUsername());

        createNewUser(user.getEmail(), user.getPassword(), hashPassword, formatedUsername, user.getName());
        log.info("User is created in database");
    }


    /**
     * Register User In Database
     *
     * @param email
     * @param password     for testing
     * @param hashPassword
     * @param username
     * @param name
     */
    public void createNewUser(String email, String password, String hashPassword, String username, String name) {
        User createUser = new User();

        createUser.setEmail(email);
        createUser.setActualPassword(password); //for testing

        createUser.setJoinedOn(LocalDateTime.now());
        createUser.setPassword(hashPassword);

        createUser.setUsername(username);
        createUser.setName(StringUtils.cleanPath(name));

        this.userRepository.save(createUser);

        log.info("User is saved");


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email , password);

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



    /**
     * Checks If User Already Present In database Based On Email
     *
     * @param email
     */
    public boolean checkUserIsPresentByEmail(String email) {
        userRepository.findUserByEmail(email).ifPresent(
                error -> {
                    throw new RuntimeException("User already present by Credentials : " + email);
                }
        );
        return false;
    }



    /**
     * Checks If User Already Present In database Based On Username
     * @param username
     * @return
     */
    public boolean CheckUserIsPresentByUsername(String username) {
        userRepository.findUserByUsername(username).ifPresent(
                error -> {
                    throw new RuntimeException("User already present by username : " + username);
                }
        );
        return false;
    }


}

