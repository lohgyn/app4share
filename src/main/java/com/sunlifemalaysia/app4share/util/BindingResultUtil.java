package com.sunlifemalaysia.app4share.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class BindingResultUtil {

    private BindingResultUtil() {
        throw new IllegalStateException("Utility class should not be initialized");
    }

    public static String getFirstErrorDescription(final BindingResult result) {

        if (result.hasFieldErrors()) {
            final FieldError fieldError = result.getFieldErrors().get(0);
            return fieldError.getDefaultMessage();
        }

        if (result.hasErrors()) {
            final ObjectError objectError = result.getAllErrors().get(0);
            return objectError.getDefaultMessage();
        }

        return "";
    }
}
