package com.alirezanoroozi.walletisc.Validator;

import com.alirezanoroozi.walletisc.Repository.RepositoryPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueNationalIdValidator implements ConstraintValidator<UniqueNationalId, String> {

    @Autowired
    private RepositoryPerson userRepository;

    @Override
    public boolean isValid(String nationalId, ConstraintValidatorContext context) {
        if (userRepository == null) {
            return true; // or any other default value you want
        }
        return !userRepository.existsByNationalId(nationalId).orElse(false);
    }
}