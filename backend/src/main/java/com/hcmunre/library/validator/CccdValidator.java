package com.hcmunre.library.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for CCCD (12 digits)
 */
public class CccdValidator implements ConstraintValidator<ValidCccd, String> {

    @Override
    public void initialize(ValidCccd annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches("^\\d{12}$");
    }
}
