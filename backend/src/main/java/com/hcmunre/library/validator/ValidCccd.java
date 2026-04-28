package com.hcmunre.library.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates that a string is a valid Vietnamese CCCD (12 digits)
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CccdValidator.class)
@Documented
public @interface ValidCccd {
    String message() default "CCCD phải có đúng 12 chữ số";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
