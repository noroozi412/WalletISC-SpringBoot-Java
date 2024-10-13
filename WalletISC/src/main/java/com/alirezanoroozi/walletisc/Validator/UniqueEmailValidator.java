package com.alirezanoroozi.walletisc.Validator;

import com.alirezanoroozi.walletisc.Repository.RepositoryPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private RepositoryPerson userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return Optional.ofNullable(userRepository)
                .map(repo -> !repo.existsByEmail(email).orElse(false))
                .orElse(true); // یا هر مقدار پیش‌فرض دیگری که می‌خواهید
    }
}