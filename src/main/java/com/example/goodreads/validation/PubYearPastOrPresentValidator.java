package com.example.goodreads.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class PubYearPastOrPresentValidator implements ConstraintValidator<PubYearPastOrPresent, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return LocalDateTime.now().getYear() >= value;
    }
}
