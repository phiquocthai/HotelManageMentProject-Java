package com.example.hotel_management_project.Annotation;

import com.example.hotel_management_project.Annotation.Impl.CCCDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CCCDValidator.class)
public @interface CCCD {
    String message() default "Số CCCD không hợp lệ.Vui lòng nhập lại đủ 12 chữ số";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
