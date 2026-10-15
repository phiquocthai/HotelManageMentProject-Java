package com.example.hotel_management_project.Annotation;

import com.example.hotel_management_project.Annotation.Impl.SDTValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SDTValidator.class)
public @interface SDT {
    public String message() default "Số điện thoại không hợp lệ.Phải ghi đầy đủ 10 chữ số:";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
