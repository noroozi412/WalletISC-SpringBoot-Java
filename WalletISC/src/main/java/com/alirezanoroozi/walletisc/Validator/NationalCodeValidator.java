package com.alirezanoroozi.walletisc.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.IntStream;

public class NationalCodeValidator implements ConstraintValidator<ValidNationalCode, String> {

    @Override
    public void initialize(ValidNationalCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(String nationalCode, ConstraintValidatorContext context) {
        // check if input has 10 digits that all of them are not equal
        if (nationalCode == null || !nationalCode.matches("^\\d{10}$")) {
            return false;
        }

        int check = Integer.parseInt(nationalCode.substring(9, 10));

        int sum = IntStream.range(0, 9)
                .map(x -> Integer.parseInt(nationalCode.substring(x, x + 1)) * (10 - x))
                .sum() % 11;

        return (sum < 2 && check == sum) || (sum >= 2 && check + sum == 11);
    }
}
