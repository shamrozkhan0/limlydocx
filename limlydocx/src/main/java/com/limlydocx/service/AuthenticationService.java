package com.limlydocx.service;

import com.limlydocx.entity.User;
import com.limlydocx.repository.UserRepository;
import com.limlydocx.globalVariable.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Register new User in the database
     */
    public void registerUser(User user, BindingResult result , RedirectAttributes redirectAttributes) {


        if (result.hasErrors()) {
            GlobalVariable.checkFeildError(result);
        }

        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username  already Exist");
        }

        if( userRepository.findUserByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Email already Exist");
        }

            String hashPassword = passwordEncoder.encode(user.getPassword());
            System.out.println("hashPassword : " + hashPassword);

            User createUser = new User();
            createUser.setEmail(user.getEmail());

            createUser.setPassword(hashPassword);
            createUser.setAcutalPassword(user.getPassword()); // for testing

            String formatedUsername =StringUtils.cleanPath(user.getUsername());
            createUser.setUsername(formatedUsername);
            createUser.setJoinedin(LocalDateTime.now());

            createUser.setName(StringUtils.cleanPath(user.getName()));
            System.out.println("user si going to saved ");

            userRepository.save(createUser);
            System.out.println("user is saved");

    }


}

