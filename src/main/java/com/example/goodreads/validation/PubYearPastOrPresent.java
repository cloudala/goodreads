package com.example.goodreads.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PubYearPastOrPresentValidator.class)
public @interface PubYearPastOrPresent {
    // Default error message
    String message() default "Publication year must be in the past or present";

    // Can be used to group validations
    Class<?>[] groups() default {};

    // Can be used to assign custom data payloads to a constraint
    Class<? extends Payload>[] payload() default {};
}
