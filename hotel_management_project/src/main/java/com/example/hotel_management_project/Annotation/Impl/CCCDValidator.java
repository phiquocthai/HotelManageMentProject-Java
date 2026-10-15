package com.example.hotel_management_project.Annotation.Impl;

import com.example.hotel_management_project.Annotation.CCCD;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CCCDValidator implements ConstraintValidator<CCCD, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.equals("") || value.isBlank()){
            return true;
        }
        return value.matches("^[0-9]{12}$");
    }
}