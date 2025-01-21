package com.limlydocx.service;

import com.limlydocx.entity.User;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register User Signup
     * @param user
     * @param result
     * @param redirectAttributes
     */
    public void registerUser(User user, BindingResult result, RedirectAttributes redirectAttributes) {
        System.out.println("enter the process");
        GlobalVariable.checkFeildError(result);
        checkUserIsPresentByEmail(user.getEmail());

        String hashPassword = passwordEncoder.encode(user.getPassword());
        String formatedUsername = StringUtils.cleanPath(user.getUsername());

        System.out.println("hiuiquerojireo");
        createNewUser(user.getEmail(), user.getPassword(), hashPassword, formatedUsername, user.getName());
        System.out.println("user is saved"); // for testing
    }



    /**
     * It will create new user with properly formated and encoded data
     *
     * @param email
     * @param password for testing
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
        System.out.println("user si going to saved ");
        userRepository.save(createUser);
    }



    /**
     * Checks If user already present in database based on email
     * @param email
     */
    public boolean checkUserIsPresentByEmail(String email){
        userRepository.findUserByEmail(email).ifPresent(
                user -> {
                    throw new RuntimeException("User already present by email :" + email);
                }
        );
        return false;
    }






}

