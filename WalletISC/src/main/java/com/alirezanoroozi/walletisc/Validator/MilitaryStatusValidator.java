package com.alirezanoroozi.walletisc.Validator;

import com.alirezanoroozi.walletisc.Entity.Person;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Calendar;
import java.util.Date;

public class MilitaryStatusValidator implements ConstraintValidator<ValidMilitaryStatus, Person> {
    @Override
    public boolean isValid(Person person, ConstraintValidatorContext context) {
        if (person.getGender().equals("woman")) {
            return true; // زنان مشمول نیستند
        }

        int age = getAge(person.getDateOfBirth());
        if (age > 18 && (person.getMilitaryStatus() == null || person.getMilitaryStatus().isEmpty())) {
            context.buildConstraintViolationWithTemplate("Military status is required for men over 18")
                    .addConstraintViolation();
            return false; // آقایان بالای 18 سال باید وضعیت سربازی را مشخص کنند
        }

        return true;
    }

    private int getAge(Date dateOfBirth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        int yearOfBirth = calendar.get(Calendar.YEAR);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return currentYear - yearOfBirth;
    }

}