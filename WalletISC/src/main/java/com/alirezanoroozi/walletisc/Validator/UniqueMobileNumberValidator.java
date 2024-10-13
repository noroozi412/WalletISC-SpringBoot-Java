package com.alirezanoroozi.walletisc.Validator;

import com.alirezanoroozi.walletisc.Repository.RepositoryPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueMobileNumberValidator implements ConstraintValidator<UniqueMobileNumber, String> {

    @Autowired
    private RepositoryPerson userRepository;

    @Override
    public boolean isValid(String mobileNumber, ConstraintValidatorContext context) {
        if (userRepository == null) {
            return true; // or any other default value you want
        }
        return !userRepository.existsByMobileNumber(mobileNumber).orElse(false);
    }
}