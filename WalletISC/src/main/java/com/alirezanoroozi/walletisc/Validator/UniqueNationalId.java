package com.alirezanoroozi.walletisc.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNationalIdValidator.class)
public @interface UniqueNationalId {
    String message() default "National code already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}