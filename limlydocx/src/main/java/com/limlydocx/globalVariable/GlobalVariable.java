package com.limlydocx.globalVariable;

import com.limlydocx.entity.User;
import com.limlydocx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public  class GlobalVariable {

    private final UserRepository userRepository;



    /**
     * Validates the given BindingResult for field errors and throws an exception if any are found.
     * @param bindingResult
     */
    public void checkFeildError(BindingResult bindingResult){
       if(bindingResult.hasErrors()){
           for (FieldError error : bindingResult.getFieldErrors()) {
               throw new RuntimeException(error.getField() + " : " + error.getDefaultMessage());
           }
       }
    }



    /**
     * get the username from the database
     * @param authentication
     * @return Username
     */
    public String getUsername(Authentication authentication){
        Optional<User> user = userRepository.findUserByEmail(authentication.getName());
        return user.get().getUsername();
    }



}
