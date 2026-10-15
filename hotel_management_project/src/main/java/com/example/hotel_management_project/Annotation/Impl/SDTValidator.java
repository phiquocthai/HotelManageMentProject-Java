package com.example.hotel_management_project.Annotation.Impl;

import com.example.hotel_management_project.Annotation.CCCD;
import com.example.hotel_management_project.Annotation.SDT;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SDTValidator implements ConstraintValidator<SDT, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.equals("") || value.isBlank()) {
            return true;
        }
        // Check if the value is a valid phone number (10 digits)
        return value.matches("^\\d{10}$");
    }
}
