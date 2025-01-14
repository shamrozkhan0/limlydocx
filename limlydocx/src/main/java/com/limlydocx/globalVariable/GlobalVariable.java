package com.limlydocx.globalVariable;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
public final class GlobalVariable {



    /**
     * Validates the given BindingResult for field errors and throws an exception if any are found.
     * @param bindingResult
     */
    public static void checkFeildError(BindingResult bindingResult){
       if(bindingResult.hasErrors()){
           for (FieldError error : bindingResult.getFieldErrors()) {
               throw new RuntimeException(error.getField() + " : " + error.getDefaultMessage());
           }
       }
    }




}
