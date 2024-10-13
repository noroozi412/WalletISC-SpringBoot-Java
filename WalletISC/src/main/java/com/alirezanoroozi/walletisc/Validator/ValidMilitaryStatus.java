package com.alirezanoroozi.walletisc.Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Constraint(validatedBy = MilitaryStatusValidator.class)
@Target(ElementType.TYPE) // Change from ElementType.FIELD to ElementType.TYPE
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMilitaryStatus {
    String message() default "Military status is required for men over 18";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}