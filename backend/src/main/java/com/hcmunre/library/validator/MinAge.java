package com.hcmunre.library.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates that the age is at least 15 years old
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinAgeValidator.class)
@Documented
public @interface MinAge {
    int value() default 15;

    String message() default "Người dùng phải >= {value} tuổi";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
